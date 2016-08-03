package edu.wvu.solar.oasisserver.plugins;

import java.util.concurrent.ConcurrentMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.mapdb.DB.HashMapMaker;

public class EventManager {
	private static final String MAP_NAME = "eventMap";
	private DB database;
	private ConcurrentMap<String, JSONArray> map;
	
	public EventManager(String dbPath){
		database = DBMaker.fileDB(dbPath).closeOnJvmShutdown().fileMmapEnableIfSupported().make();
	}
	
	public void registerEvent(JSONObject registerEvent){
		if(registerEvent.getString("type").length() > 0 && registerEvent.getJSONArray("parameters").length() > 0){
			//open hash map
			JSONArraySerializer serializer = new JSONArraySerializer();
			HashMapMaker<String, JSONArray> mapMaker = database.hashMap(MAP_NAME, Serializer.STRING, serializer);
			map=mapMaker.createOrOpen();
			
			//write to hash map and save
			map.put(registerEvent.getString("type"), registerEvent.getJSONArray("parameters"));
			database.commit();
		}
	}
	
	public static void main(String args[]){
		EventManager test=new EventManager("/Users/TestSolarDB/dataStorage.db");
		
		JSONArray employees = new JSONArray();
		employees.put(true);
		
		JSONObject hello = new JSONObject();
		hello.put("type", "hello there");
		hello.put("parameters",employees);
		test.registerEvent(hello);
	}
}