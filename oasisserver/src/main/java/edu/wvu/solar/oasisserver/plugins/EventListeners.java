package edu.wvu.solar.oasisserver.plugins;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mapdb.DB;
import org.mapdb.DB.HashMapMaker;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import edu.wvu.solar.oasisserver.plugins.exceptions.InvalidEventException;
import edu.wvu.solar.oasisserver.plugins.exceptions.InvalidParametersException;


/**
 * This class provides the "If This Then That" functionality
 * of the system.
 *
 */
public class EventListeners {

	//private static final Logger LOGGER = LogManager.getLogger(EventListeners.class);
	public static final String RECIPE_ID_LABEL = "recipeID";
	public static final String DEVICE_ID_LABEL = "deviceID";
	public static final String PARAMETERS_LABEL = "parameters";
	public static final String TYPE_LABEL = "type";
	public static final String EVENT_LABEL = "event";
	public static final String VALUE_LABEL = "value";
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
	
	public EventListeners(String dbPath){
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
	 * @param event Event to trigger this recipe. In order to trigger the recipe,
	 * 		all data in this JSONObject must match the event exactly. This event
	 * 		must have a "type" parameter. Every other parameter is optional.
	 * @param deviceID ID of device to edit the parameters of when this recipe is triggered
	 * @param parameters Parameters to set on the specified device when this recipe is triggered
	 * 
	 * @return Randomly generated ID that was given to the newly-added recipe
	 * 
	 * @throws InvalidParametersException If parameters is an empty array
	 * @throws InvalidEventException If event does not have a "type" parameter
	 */
	public String addRecipe(JSONObject event, String deviceID, JSONArray parameters){
		//TODO: Check if deviceID is a valid device ID.
		//TODO: More sanity checking, like within the event
		if(parameters.length() <= 0){
			throw new InvalidParametersException("No parameters supplied.");
		}
		if(!event.has("type")){
			throw new InvalidEventException("Event must contain \"type\" parameter.");
		}
		
		String recipeID = UUID.randomUUID().toString();
		JSONObject recipe = new JSONObject();
		recipe.put(RECIPE_ID_LABEL, recipeID);
		recipe.put(DEVICE_ID_LABEL, deviceID);
		recipe.put(PARAMETERS_LABEL, parameters);
		recipe.put(EVENT_LABEL, event);
		String type = event.getString(TYPE_LABEL);
		
		JSONArray list;
		if(map.containsKey(type)){
			LOGGER.debug("addRecipe: Found existing list for type {}", type);
			list = map.get(type);
			//list = new JSONArray(oldListString);
		}else{
			LOGGER.debug("addRecipe: Making new list for type {}", type);
			list = new JSONArray();
		}
		list.put(recipe);
		map.put(type, list);
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
	 *@param type the event type of the recipe
	 */
	public void removeRecipe(String recipeId, String type){
		JSONArray array = map.get(type);
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
				map.put(type, array);
				database.commit();
			}
		}
	}
	
	/**
	 * This method is called when an event is triggered. It searches the database for any
	 * recipes that match this event, and acts accordingly
	 * 
	 * @param event Event that was triggered
	 * @return List of RecipeIDs that were triggered
	 */
	public List<String> eventTriggered(JSONObject triggeredEvent){
		if(!triggeredEvent.has(TYPE_LABEL)){
			throw new InvalidEventException("Event must contain a 'type' parameter.");
		}
		
		List<String> triggeredIDs = new ArrayList<String>();
		
		String type = triggeredEvent.getString(TYPE_LABEL);
		JSONArray array = map.get(type);
		JSONArray triggeredParameters = triggeredEvent.getJSONArray(PARAMETERS_LABEL);
		JSONObject triggeredParameter;
		
		if(array != null && triggeredParameters != null){
			JSONObject recipe;
			JSONArray recipeParameters;
			JSONObject recipeParameter;
			
			
			for(int recipeNum = 0; recipeNum < array.length(); recipeNum++){
				//This for loop checks all recipes of the given type...
				recipe = array.getJSONObject(recipeNum);
				recipeParameters = recipe.getJSONObject(EVENT_LABEL).getJSONArray(PARAMETERS_LABEL);
				
				//This value will be set to false if a recipe parameter is found that doesn't
				//have a matching event parameter. In that case, we don't have to bother
				//searching through the rest of the recipe parameters
				boolean continueSearching = true;
				
				for(int recipeParamNum = 0; recipeParamNum < recipeParameters.length() && continueSearching; recipeParamNum++){
					//This for loop goes through all parameters in the current recipe...
					recipeParameter = recipeParameters.getJSONObject(recipeParamNum);
					
					//This will be set to true when an event parameter is found that matches the
					//current recipe parameter
					boolean matchFound = false;
					
					for(int triggeredParamNum = 0; triggeredParamNum < triggeredParameters.length() && !matchFound; triggeredParamNum++){
						//And this loop goes through all the parameters in the newly triggered event to see
						//if one of them matches the current recipe parameter.
						//(All recipe parameters have to find a match in the triggered event parameters
						//in order for the recipe to be executed)
						triggeredParameter = triggeredParameters.getJSONObject(triggeredParamNum);
						if(triggeredParameter.getString(NAME_LABEL).equals(recipeParameter.getString(NAME_LABEL))
								&& triggeredParameter.getString(TYPE_LABEL).equals(recipeParameter.getString(TYPE_LABEL))
								&& triggeredParameter.get(VALUE_LABEL).equals(recipeParameter.get(VALUE_LABEL))){
							
							matchFound = true;
						}
					}
					
					if(!matchFound){
						continueSearching = false;
					}
				}
				
				//If this variable is false, it means we stopped searching through the
				//recipe parameters because we found one without a matching event parameter.
				//If it's true, it means this recipe DOES match the event, so we need to 
				//do stuff
				if(continueSearching){
					LOGGER.debug("Time to do stuff with this recipe:\nRecipe: {}\nEvent: {}", recipe.toString(2), triggeredEvent.toString(2));
					triggeredIDs.add(recipe.getString(RECIPE_ID_LABEL));
					//TODO: Change the parameters
				}
			}
		}
		
		return triggeredIDs;
	}
	
	
	
	
	
	public static void main(String[] args) throws InvalidParametersException, InvalidEventException{
			
		//Note: In Eclipse, multiple lines can be commented/uncommented at once by highlighting them
		//and hitting ctrl+/ (or cmd+/ on Mac)
		
//		DB database = DBMaker.fileDB("/Users/Timmy/Desktop/TESTDBBB.db").closeOnJvmShutdown().fileMmapEnableIfSupported().make();
//		
//		HashMapMaker<String, String> mapMaker = database.hashMap(MAP_NAME, Serializer.STRING, Serializer.STRING);
//		ConcurrentMap<String, String> map;
//		if(database.exists(MAP_NAME)){
//			//LOGGER.debug("Opening existing entry");
//			map = mapMaker.open();
//			
//		}else{
//			//LOGGER.debug("Creating new entry");
//			map = mapMaker.create();
//		}
//		
//		/*map.put("Test1", "TESTIFICATE UNO");
//		database.commit();
//		map.put("Test2", "TESTIFICATE DOS");
//		database.commit();*/
//		System.out.println(map.get("Test1"));
//		System.out.println(map.get("Test2"));
//		
//		long start = System.currentTimeMillis();
//		for(int i = 0; i < 1000; i++){
//			map.put("Test", "TESTIFICATE " + i);
//			database.commit();
//		}
//		//database.commit();
//		long end = System.currentTimeMillis();
//		System.out.println(end-start);
		
		EventListeners listeners = new EventListeners("/Users/Timmy/Desktop/AAAAAHHHHH.db");
		JSONArray parameters = new JSONArray("[{'name':'test1','value':'testvalue','type':'testtype'},{'name':'test2','value':'testvalue','type':'testtype'}]");
		JSONObject event = new JSONObject("{'type':'test'}");
		event.put("parameters", parameters);
		listeners.addRecipe(event, "1234", parameters);
		JSONArray otherparameters = new JSONArray("[{'name':'test1','value':'testvalue2','type':'testtype'},{'name':'test2','value':'testvalue','type':'testtype'}]");
		JSONObject otherEvent = new JSONObject("{'type':'test'}");
		otherEvent.put("parameters", otherparameters);
		listeners.eventTriggered(otherEvent);
	}
}
