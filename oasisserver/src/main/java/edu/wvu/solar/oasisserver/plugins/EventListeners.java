package edu.wvu.solar.oasisserver.plugins;

import java.util.HashMap;
import java.util.UUID;
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


/**
 * This class provides the "If This Then That" functionality
 * of the system.
 *
 */
public class EventListeners {

	//private static final Logger LOGGER = LogManager.getLogger(EventListeners.class);
	private static final String RECIPE_ID_LABEL = "recipeID";
	private static final String DEVICE_ID_LABEL = "deviceID";
	private static final String PARAMETERS_LABEL = "parameters";
	private static final String TYPE_LABEL = "type";
	private static final String MAP_NAME = "map";

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
	private HashMap<String, JSONArray> recipes;
	private DB database;
	
	public EventListeners(String dbPath){
		database = DBMaker.fileDB(dbPath).closeOnJvmShutdown().fileMmapEnableIfSupported().make();
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
	public void addRecipe(JSONObject event, String deviceID, JSONArray parameters) throws InvalidParametersException, InvalidEventException{
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
		/*if(recipes.get(type) == null){
			recipes.put(type, new JSONArray());
		}
		
		recipes.get(type).put(recipe);*/
		
		ConcurrentMap<String, String> map;
		HashMapMaker<String, String> mapMaker = database.hashMap(MAP_NAME, Serializer.STRING, Serializer.STRING);
		if(database.exists(MAP_NAME)){
			//LOGGER.debug("Opening existing entry");
			map = mapMaker.open();
			
		}else{
			//LOGGER.debug("Creating new entry");
			map = mapMaker.create();
		}
		map.put(type, recipe.toString());
		database.commit();
	}
	/* Retrieves the values of all the JSON strings stored in the database
	 * 
	 * @return all the JSON string values in a single string
	 * 
	 */
	public String getRecipes(){
			ConcurrentMap<String, String> map;
			HashMapMaker<String, String> mapMaker = database.hashMap(MAP_NAME, Serializer.STRING, Serializer.STRING);
			map = mapMaker.createOrOpen();
			return map.values().toString();
			

	}
	/**
	 *Removes the specified value from the database then saves the change to disk
	 *
	 *@param key the key value that matches up with the value desired to move. 
	 *		In this case the key is the event type.
	 *
	 *@return returns the String of the object that is removed or null if there 
	 *		  nothing removed
	 * 
	 */
	public String removeRecipe(String key){
		String r = null;
		ConcurrentMap<String, String> map;
		HashMapMaker<String, String> mapMaker = database.hashMap(MAP_NAME, Serializer.STRING, Serializer.STRING);
		map = mapMaker.createOrOpen();
		r = map.remove(key);
		database.commit();
		return r;
	}
	public static void main(String[] args) throws InvalidParametersException, InvalidEventException{
		/*DB db = DBMaker.fileDB("/Users/Timmy/Desktop/test.db").fileMmapEnable().make();
		ConcurrentMap<Integer, String> map = db.hashMap("TEST").keySerializer(Serializer.INTEGER).valueSerializer(Serializer.STRING).open();
		for(int i = 0; i < 10000; i++){
			map.put(i, "test " + i);
		}
		db.close();
		
		DB db2 = DBMaker.fileDB("/Users/Timmy/Desktop/test.db").fileMmapEnable().make();
		
		ConcurrentMap<Integer, String> map2;
		map2 = db2.hashMap("TEST").keySerializer(Serializer.INTEGER).valueSerializer(Serializer.STRING).open();
		
		String test = map2.get(7);
		System.out.println(test);
		db.close();
		long begin = System.currentTimeMillis();
		for(int i = 0; i < 1000000; i++){
			map2 = db2.hashMap("TEST").keySerializer(Serializer.INTEGER).valueSerializer(Serializer.STRING).open();
		}
		long end = System.currentTimeMillis();
		System.out.println(end - begin);*/

		JSONArray parameters = new JSONArray("[{\"name\":\"on\", \"type\":\"boolean\"}, {\"name\":\"color\", \"type\":\"color\"}]");
		JSONObject event = new JSONObject("{\"state\":\"off\", \"type\":\"test\"}");
		String deviceID = "Blep";
		EventListeners el = new EventListeners("/home/chrx/Documents/test2.db");
		long start = System.currentTimeMillis();
		
		for(int i = 0; i < 10; i++){
			JSONObject t = event;
			t.put(TYPE_LABEL, Integer.toString(i));
			el.addRecipe(t, deviceID+i, parameters);
		}
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
				
		for(int i = 0; i < 10; i++){
			System.out.println(el.removeRecipe(Integer.toString(i)));
		}
		
		System.out.println(el.getRecipes());
	}
}
