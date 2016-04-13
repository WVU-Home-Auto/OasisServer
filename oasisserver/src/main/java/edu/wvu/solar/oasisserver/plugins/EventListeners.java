package edu.wvu.solar.oasisserver.plugins;

import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;


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
	
	/*
	 * This stores the recipes with their unique ID as the key.
	 * Each JSONObject is formatted like this:
	 *  {
	 * 		recipeID: <unique ID>
	 * 		deviceID: <Device ID to edit parameters of>
	 * 		parameters: [
	 * 			<array of parameters to set on device referenced by deviceID>
	 * 		]
	 *  }
	 */
	private static HashMap<String, JSONObject> recipes;
	
	static{
		//TODO: Load recipes from file
		recipes = new HashMap<String, JSONObject>();
		LOGGER.debug("Am not yet loading recipes from file.");
	}

	/**
	 * Adds the specified recipe to the database, so that when an event is triggered
	 * that matches the event parameter, the device specified by the deviceID parameter
	 * has its  parameters edited according to the parameters parameter
	 * 
	 * @param event Event to trigger this recipe. In order to trigger the recipe,
	 * 		all data in this JSONObject must match the event exactly.
	 * @param deviceID ID of device to edit the parameters of when this recipe is triggered
	 * @param parameters Parameters to set on the specified device when this recipe is triggered
	 * @return Unique ID of the newly created recipe
	 */
	public static String addRecipe(JSONObject event, String deviceID, JSONArray parameters){
		String recipeID = UUID.randomUUID().toString();
		JSONObject recipe = new JSONObject();
		recipe.append(RECIPE_ID_LABEL, recipeID);
		recipe.append(DEVICE_ID_LABEL, deviceID);
		recipe.append(PARAMETERS_LABEL, parameters);
		recipes.put(recipeID, recipe);
		return recipeID;
	}
}
