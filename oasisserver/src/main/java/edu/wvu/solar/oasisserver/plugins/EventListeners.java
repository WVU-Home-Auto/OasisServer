package edu.wvu.solar.oasisserver.plugins;

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

import javax.print.attribute.standard.MediaSize;

public class EventListeners {
	
	private static final Logger LOGGER = LogManager.getLogger(EventListeners.class);
	
	private String eventType;
	private List<ParameterComparison> comparisons;
	private List<Parameter> parameters;
	private DeviceManager deviceManager;
	
	private DB database;
	private static ConcurrentMap<String, List<Recipe>> recipes;
    private static final String MAP_NAME = "eventMap";
	
	public EventListeners(String eventType, List<ParameterComparison> comparisons, List<Parameter> parameters, DeviceManager deviceManager, String dbPath, PluginManager pluginManager){
		this.eventType = eventType;
		this.comparisons = comparisons;
		this.parameters = parameters;
		this.deviceManager = deviceManager;
		
		database = DBMaker.fileDB(dbPath).closeOnJvmShutdown().fileMmapEnableIfSupported().make();

		RecipeSerializer serializer = new RecipeSerializer();
        HashMapMaker<String, List<Recipe>> mapMaker = database.hashMap(MAP_NAME, Serializer.STRING, serializer);
        if(database.exists(MAP_NAME)){
            LOGGER.debug("Opening existing hashmap in EventListener constructor");
            recipes = mapMaker.open();
        }else{
            LOGGER.debug("Creating new hashmap in EventListener constructor");
            recipes = mapMaker.create();
        }
	}
	
    public void addRecipe(String eventType, List<ParameterComparison> comparisons, List<Parameter> parameters, DeviceManager deviceManager){
    	List<Recipe> fromDB = recipes.get(eventType);
    	Recipe temp = new Recipe(eventType, comparisons, parameters, deviceManager);
    	if(fromDB == null){
    		fromDB = new ArrayList<Recipe>();
    	}
    	fromDB.add(temp);
    	
    	//TODO: Test to see if you need to commit to the database
    }
    
    public void eventTriggered(Event event){
    	//TODO: do this
    }
    //Event is an unknown thing
    
    
}
