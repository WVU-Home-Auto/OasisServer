package edu.wvu.solar.oasisserver.plugins;

import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.mapdb.DB.HashMapMaker;

/**
 * This class will maintain a list of all devices in the house,
 * and facilitate communication between them, including events
 * and parameters.
 *
 */
public class DeviceManager {
	
	//private HashMap<String, JSONArray> recipes;
		private DB database;
		private static ConcurrentMap<String, JSONObject> map;
		private static final String MAP_NAME = "deviceMap";
		private static final Logger LOGGER = LogManager.getLogger(DeviceManager.class);
		
		public DeviceManager(String dbPath){
			database = DBMaker.fileDB(dbPath).closeOnJvmShutdown().fileMmapEnableIfSupported().make();
			
			JSONObjectSerializer serializer = new JSONObjectSerializer();
			HashMapMaker<String, JSONObject> mapMaker = database.hashMap(MAP_NAME, Serializer.STRING, serializer);
			if(database.exists(MAP_NAME)){
				LOGGER.debug("Opening existing hashmap in EventListener constructor");
				map = mapMaker.open();
			}else{
				LOGGER.debug("Creating new hashmap in EventListener constructor");
				map = mapMaker.create();
			}
		}
    /**
     * @param deviceID ID of deivce to check parameters of
     * @return The result of calling getParameters() on the specified device
     */
    public static JSONArray getParameters(String deviceID){
        return map.get(deviceID).getJSONArray("parameters");
    }

    /**
     * Simply calls setParameters() on the specified device
     * @param deviceID ID of device of which to set parameters
     * @param parameters Parameters to be set. This could only contain those parameters you wish to change,
     *                   or could also include the other parameters that don't change.
     */
    public void setParameters(String deviceID, JSONArray parameters){

    }
    
    /** Retrieves the values of all the recipes stored in the database
	 * 
	 * @return a JSONArray of all recipes. Each recipe has 
	 * parameters recipeID, deviceID, parameters, and event
	 * 
	 */
	public JSONArray getDevices(){
		JSONArray output = new JSONArray();	
		for(String list : map.keySet()){
			JSONObject o = new JSONObject();
			o.put("ID", list);
			o.put("parameters", map.get(list));
			output.put(o);
		}
		return output;
	}
}
