package edu.wvu.solar.oasisserver.plugins;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
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
	private ConcurrentMap<String, String> map;
	
	public EventListeners(String dbPath){
		database = DBMaker.fileDB(dbPath).closeOnJvmShutdown().fileMmapEnableIfSupported().make();
		
		HashMapMaker<String, String> mapMaker = database.hashMap(MAP_NAME, Serializer.STRING, Serializer.STRING);
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
			String oldListString = map.get(type);
			list = new JSONArray(oldListString);
		}else{
			LOGGER.debug("addRecipe: Making new list for type {}", type);
			list = new JSONArray();
		}
		list.put(recipe);
		map.put(type, list.toString());
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
		for(String json : map.values()){
			JSONArray list = new JSONArray(json);
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
		String json = map.get(type);
		if(json != null){
			JSONArray array = new JSONArray(json);
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
				json = array.toString();
				map.put(type, json);
				database.commit();
			}
		}
	}
	
	/**
	 * This method is called when an event is triggered. It searches the database for any
	 * recipes that match this event, and acts accordingly
	 * 
	 * @param event Event that was triggered
	 */
	public void eventTriggered(JSONObject event){
		if(!event.has(TYPE_LABEL)){
			throw new InvalidEventException("Event must contain a 'type' parameter.");
		}
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
		
		JSONArray arr1 = new JSONArray();
		arr1.put("one");
		arr1.put("two");
		JSONArray arr2 = new JSONArray();
		arr2.put("three");
		arr2.put("four");
		arr1.put(arr2);
		System.out.println(arr1.toString(4));
	}
}
