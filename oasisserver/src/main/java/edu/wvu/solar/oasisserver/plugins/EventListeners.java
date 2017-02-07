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
	
	public EventListeners(String eventType, List<ParameterComparison> comparisons, List<Parameter> parameters, DeviceManager deviceManager){
		this.eventType = eventType;
		this.comparisons = comparisons;
		this.parameters = parameters;
		this.deviceManager = deviceManager;
	}
	
    public void addRecipe(String eventType, List<ParameterComparison> comparisons, List<Parameter> parameters, DeviceManager deviceManager){
    	Recipe(eventType, comparisons, parameters, deviceManager);
    }
    
    public void eventTriggered(Event event){
    	//TODO: do this
    }
    //Event is an unknown thing
    
    
}
