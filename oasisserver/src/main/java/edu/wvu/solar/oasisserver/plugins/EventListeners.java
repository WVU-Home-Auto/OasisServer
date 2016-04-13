package edu.wvu.solar.oasisserver.plugins;

import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.wvu.solar.oasisserver.plugins.exceptions.InvalidEventException;
import edu.wvu.solar.oasisserver.plugins.exceptions.InvalidParametersException;


/**
 * This class provides the "If This Then That" functionality
 * of the system.
 *
 */
public class EventListeners {

	private static final Logger LOGGER = LogManager.getLogger(EventListeners.class);
	private static final String RECIPE_ID_LABEL = "recipeID";
	private static final String DEVICE_ID_LABEL = "deviceID";
	private static final String PARAMETERS_LABEL = "parameters";
	private static final String TYPE_LABEL = "type";
	
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
	private static HashMap<String, JSONArray> recipes;
	
	static{
		//TODO: Load recipes from file
		recipes = new HashMap<String, JSONArray>();
		LOGGER.debug("Am not yet loading recipes from file.");
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
	 * @throws InvalidParametersException If parameters is an empty array
	 * @throws InvalidEventException If event does not have a "type" parameter
	 */
	public static void addRecipe(JSONObject event, String deviceID, JSONArray parameters) throws InvalidParametersException, InvalidEventException{
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
		String type = event.getString(TYPE_LABEL);
		if(recipes.get(type) == null){
			recipes.put(type, new JSONArray());
		}
		
		recipes.get(type).put(recipe);
	}
}
