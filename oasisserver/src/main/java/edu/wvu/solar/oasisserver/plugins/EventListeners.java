package edu.wvu.solar.oasisserver.plugins;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mapdb.DB;
import org.mapdb.DB.HashMapMaker;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import edu.wvu.solar.oasisserver.plugins.exceptions.InvalidEventException;
import edu.wvu.solar.oasisserver.plugins.exceptions.InvalidParametersException;

import javax.print.attribute.standard.MediaSize;


/**
 * This class provides the "If This Then That" functionality
 * of the system.
 *
 */
public class EventListeners {

	//private static final Logger LOGGER = LogManager.getLogger(EventListeners.class);
	public static final String RECIPE_ID_LABEL = "recipeID";
	public static final String DEVICE_ID_LABEL = "deviceID";
	//public static final String PARAMETERS_LABEL = "parameters";
    public static final String CHECK_PARAMETERS_LABEL = "checkParameters";
    public static final String SET_PARAMETERS_LABEL = "setParameters";
	public static final String EVENT_TYPE_LABEL = "eventType";
	public static final String EVENT_LABEL = "event";
	public static final String VALUE_LABEL = "value";
    public static final String PARAMETER_TYPE_LABEL = "type";
    public static final String COMPARISON_LABEL = "comparison";
	public static final String NAME_LABEL = "name";
	private static final String MAP_NAME = "recipesMap";
	private static final Logger LOGGER = LogManager.getLogger(EventListeners.class);

	/*
	 * This stores the recipes with the event type as the key, and
	 * a JSONArray of all recipes with this event type as the value.
	 * 
	 * Each JSONObject in the JSONArray is formatted like this:
	 *  {
	 * 		recipeID: <unique ID>
	 * 		deviceID: <Device ID to edit parameters of>
	 * 		parameters: [
	 * 			<array of parameters to set on device referenced by deviceID>
	 * 		]
	 *  }
	 */
	//private HashMap<String, JSONArray> recipes;
	private DB database;
	private ConcurrentMap<String, JSONArray> map;

    // Stores a reference to the one and only instance of DeviceManager
    private DeviceManager deviceManager;
	
	public EventListeners(String dbPath, DeviceManager deviceManager){

	    this.deviceManager = deviceManager;

		database = DBMaker.fileDB(dbPath).closeOnJvmShutdown().fileMmapEnableIfSupported().make();
		
		JSONArraySerializer serializer = new JSONArraySerializer();
		HashMapMaker<String, JSONArray> mapMaker = database.hashMap(MAP_NAME, Serializer.STRING, serializer);
		if(database.exists(MAP_NAME)){
			LOGGER.debug("Opening existing hashmap in EventListener constructor");
			map = mapMaker.open();
		}else{
			LOGGER.debug("Creating new hashmap in EventListener constructor");
			map = mapMaker.create();
		}
	}

	/**
	 * Adds the specified recipe to the database, so that when an event is triggered
	 * that matches the event parameter, the device specified by the deviceID parameter
	 * has its  parameters edited according to the parameters parameter
	 * 
	 * @param eventType Type of event to listen for.
     * @param deviceID ID of the device to listen for. If this is left null, then this recipe listens
     *                 for ANY device that triggers this type of event
	 * @param checkParameters All parameter checks must be met in order for this event to be triggered.
	 * @param setParameters Parameters to set when this recipe is triggered. Each parameter
     *                      should specify a deviceID field
	 * 
	 * @return Randomly generated ID that was given to the newly-added recipe
	 * 
	 * @throws InvalidParametersException If parameters is an empty array
	 * @throws InvalidEventException If event does not have a "type" parameter
	 */
	public String addRecipe(String eventType, String deviceID, JSONArray checkParameters, JSONArray setParameters){
		//TODO: Check if eventType is valid
		//TODO: More sanity checking, like within the parameters
		if(checkParameters.length() <= 0){
			throw new InvalidParametersException("No check parameters supplied.");
		}
		if(setParameters.length() <= 0){
		    throw new InvalidParametersException("No set parameters supplied.");
        }
		for(int i = 0; i < checkParameters.length(); i++){
		    if(!checkParameters.getJSONObject(i).has(DEVICE_ID_LABEL)){
		        throw new InvalidParametersException("All parameters must contain a \"deviceID\" field.");
            }
        }
        for(int i = 0; i < setParameters.length(); i++){
            if(!setParameters.getJSONObject(i).has(DEVICE_ID_LABEL)){
                throw new InvalidParametersException("All parameters must contain a \"deviceID\" field.");
            }
        }
		
		String recipeID = UUID.randomUUID().toString();
		JSONObject recipe = new JSONObject();
		recipe.put(RECIPE_ID_LABEL, recipeID);
        recipe.put(CHECK_PARAMETERS_LABEL, checkParameters);
		recipe.put(SET_PARAMETERS_LABEL, setParameters);
		recipe.put(EVENT_TYPE_LABEL, eventType);
        if(deviceID != null){
            recipe.put(DEVICE_ID_LABEL, deviceID);
        }

		JSONArray list;
		if(map.containsKey(eventType)){
			LOGGER.debug("addRecipe: Found existing list for type {}", eventType);
			list = map.get(eventType);
			//list = new JSONArray(oldListString);
		}else{
			LOGGER.debug("addRecipe: Making new list for type {}", eventType);
			list = new JSONArray();
		}
		list.put(recipe);
		map.put(eventType, list);
		database.commit();
		return recipeID;
	}
	
	
	/** Retrieves the values of all the recipes stored in the database
	 * 
	 * @return a JSONArray of all recipes. Each recipe has 
	 * parameters recipeID, deviceID, parameters, and event
	 * 
	 */
	public JSONArray getRecipes(){
		JSONArray output = new JSONArray();	
		for(JSONArray list : map.values()){
			//JSONArray list = new JSONArray(json);
			for(int i = 0; i < list.length(); i++){
				output.put(list.getJSONObject(i));
			}
		}
		return output;
	}


	/**
	 *Removes the specified value from the database then saves the change to disk
	 *
	 *@param recipeId the ID of the recipe that was randomly generated on recipe creation
	 *@param eventType the event type of the recipe
	 */
	public void removeRecipe(String recipeId, String eventType){
		JSONArray array = map.get(eventType);
		if(array != null){
			//JSONArray array = new JSONArray(json);
			int toRemove = -1;
			for(int i = 0; i < array.length(); i++){
				JSONObject recipe = array.getJSONObject(i);
				if(recipe.getString(RECIPE_ID_LABEL).equals(recipeId)){
					toRemove = i;
					break;
				}
			}

			if(toRemove >= 0){
				array.remove(toRemove);
				//json = array.toString();
				map.put(eventType, array);
				database.commit();
			}
		}
	}


	/**
	 * This method is called when an event is triggered. It searches the database for any
	 * recipes that match this event, and acts accordingly
	 * 
	 * @param triggeredEvent Event that was triggered
	 * @return List of RecipeIDs that were triggered
	 */
	List<String> eventTriggered(JSONObject triggeredEvent){
		if(!triggeredEvent.has(EVENT_TYPE_LABEL)){
			throw new InvalidEventException("Event must contain a 'type' parameter.");
		}

		// This is the ID of the device that triggered the event, or null if none is specified
		String triggeredDeviceID = triggeredEvent.has(DEVICE_ID_LABEL) ? triggeredEvent.getString(DEVICE_ID_LABEL) : null;
		
		List<String> triggeredIDs = new ArrayList<String>();
		
		String eventType = triggeredEvent.getString(EVENT_TYPE_LABEL);

        // List of Recipes with the correct event type
        JSONArray recipes = map.get(eventType);

		if(recipes != null){
			JSONObject recipe;
			JSONArray recipeCheckParameters;
            JSONArray recipeSetParameters;
            String recipeDeviceID;
			JSONObject checkParameter;

			for(int recipeNum = 0; recipeNum < recipes.length(); recipeNum++){
				//This for loop checks all recipes of the given type...
				recipe = recipes.getJSONObject(recipeNum);
				recipeCheckParameters = recipe.getJSONArray(CHECK_PARAMETERS_LABEL);
                recipeSetParameters = recipe.getJSONArray(SET_PARAMETERS_LABEL);
                recipeDeviceID = recipe.has(DEVICE_ID_LABEL) ? recipe.getString(DEVICE_ID_LABEL) : null;
				
				//This value will be set to false if a recipe parameter is found that doesn't
				//have a matching event parameter. In that case, we don't have to bother
				//searching through the rest of the recipe parameters
                //It starts out as false IFF the recipe has a deviceID which is not equal to the deviceID that
                //triggered this method. In this case, don't even bother checking the parameters, because the
                //deviceID is wrong.
				boolean goodSoFar = recipeDeviceID == null || recipeDeviceID.equals(triggeredDeviceID);
				
				for(int checkParamNum = 0; checkParamNum < recipeCheckParameters.length() && goodSoFar; checkParamNum++){
					//This for loop goes through all parameters in the current recipe...
					checkParameter = recipeCheckParameters.getJSONObject(checkParamNum);
                    goodSoFar = parameterCheck(checkParameter);
				}
				
				//If this variable is false, it means we stopped searching through the
				//recipe parameters because we found one without a matching  parameter.
				//If it's true, it means this recipe DOES match the event, so we need to 
				//do stuff
				if(goodSoFar){
					LOGGER.debug("Time to do stuff with this recipe:\nRecipe: {}\nEvent: {}", recipe.toString(2), triggeredEvent.toString(2));
					triggeredIDs.add(recipe.getString(RECIPE_ID_LABEL));
                    //In the interest of some marginal gains in efficiency, we gather together all the parameters to be set
                    //by their deviceID, so that each Device only has setParameters() called once on it.
                    HashMap<String, JSONArray> parametersToSet = new HashMap<String, JSONArray>();
					for(int i = 0; i < recipeSetParameters.length(); i++){
					    JSONObject recipeSetParameter = recipeSetParameters.getJSONObject(i);
                        String deviceId = recipeSetParameter.getString(DEVICE_ID_LABEL);
                        //I'm running out of names for variables
                        JSONArray tempArray;
                        if(parametersToSet.containsKey(deviceId)){
                            tempArray = parametersToSet.get(deviceId);
                        }else{
                            tempArray = new JSONArray();
                        }
                        tempArray.put(recipeSetParameter);
                        parametersToSet.put(deviceId, tempArray);
                    }

                    for(Map.Entry<String, JSONArray> entry : parametersToSet.entrySet()){
                        deviceManager.setParameters(entry.getKey(), entry.getValue());
                    }
				}
			}
		}
		
		return triggeredIDs;
	}

    /**
     * This looks at checkParameter, extracts the deviceID value from it, and checks that device to see if it has a
     * parameter that meets the criteria of checkParameter
     * @param checkParameter Parameter to check
     * @return True if the device specified by the deviceID field in checkParameter has such a parameter, false otherwise
     */
	private boolean parameterCheck(JSONObject checkParameter){
        String deviceID = checkParameter.getString(DEVICE_ID_LABEL);
        JSONArray realParameters = deviceManager.getParameters(deviceID);
        String comparison = checkParameter.getString(COMPARISON_LABEL);
        String checkParameterName = checkParameter.getString(NAME_LABEL);
        JSONObject realParameter;
        for(int i = 0; i < realParameters.length(); i++){
            realParameter = realParameters.getJSONObject(i);

            if(checkParameterName.equals(realParameter.getString(NAME_LABEL))) {
                if (comparison.equals("==")) {
                    return checkParameter.getString(PARAMETER_TYPE_LABEL).equals(realParameter.getString(PARAMETER_TYPE_LABEL))
                            && checkParameter.get(VALUE_LABEL).equals(realParameter.get(VALUE_LABEL));
                } else if (comparison.equals("!=")) {
                    return !(checkParameter.getString(PARAMETER_TYPE_LABEL).equals(realParameter.getString(PARAMETER_TYPE_LABEL))
                            && checkParameter.get(VALUE_LABEL).equals(realParameter.get(VALUE_LABEL)));
                    // This regex just checks to see if comparison is one of "<", ">", "<=", ">="
                } else if (comparison.matches("^[<>]=?$")) {
                    // Less than / greater than [or equal to] operators are only valid for numbers.
                    // Integers can be cast to doubles without loss of precision, but not vice-versa.
                    // Therefore, just assume everything is doubles and hope for the best.
                    double realVal = (Double) realParameter.get(VALUE_LABEL);
                    double checkVal = (Double) checkParameter.get(VALUE_LABEL);

                    if (comparison.equals("<")) {
                        return realVal < checkVal;
                    } else if (comparison.equals(">")) {
                        return realVal > checkVal;
                    } else if (comparison.equals("<=")) {
                        return realVal <= checkVal;
                    } else if (comparison.equals(">=")) {
                        return realVal >= checkVal;
                    } else {
                        LOGGER.error("This code should never have been executed.");
                        // This should never happen, but I need it here to stop the Java compiler from complaining
                        return false;
                    }
                } else {
                    throw new InvalidParametersException("\"" + comparison + "\" is not a valid comparison.");
                }
            }
        }

        return false;
    }
}
