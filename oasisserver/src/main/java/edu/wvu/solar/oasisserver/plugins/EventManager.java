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

		JSONArraySerializer serializer = new JSONArraySerializer();
		HashMapMaker<String, JSONArray> mapMaker = database.hashMap(MAP_NAME, Serializer.STRING, serializer);
		if(database.exists(MAP_NAME)){
			map = mapMaker.open();
		}else{
			map = mapMaker.create();
		}

	}

	public void registerEvent(JSONObject registerEvent){
		if(registerEvent.getString("type").length() > 0 && registerEvent.getJSONArray("parameters").length() > 0){
			//write to hash map and save
			map.put(registerEvent.getString("type"), registerEvent.getJSONArray("parameters"));
			database.commit();
		}
	}
	public JSONArray getEvents(){
		JSONArray output = new JSONArray();
		for(String list : map.keySet()){
			JSONObject o = new JSONObject();
			o.put("type", list);
			o.put("parameters", map.get(list));
			output.put(o);
		}
		return output;
	}
}