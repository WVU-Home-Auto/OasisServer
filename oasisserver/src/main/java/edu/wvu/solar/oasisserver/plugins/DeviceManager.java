package edu.wvu.solar.oasisserver.plugins;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.mapdb.DB.HashMapMaker;

public class DeviceManager {

    private DB database;
    private static final String MAP_NAME = "deviceMap";

    // This map stores all of the Devices in the system. The String key is the Device's unique ID.
    private static ConcurrentMap<String, Device> devices;

    private static final Logger LOGGER = LogManager.getLogger(DeviceManager.class);

    public DeviceManager(String dbPath, PluginManager pluginManager){
        database = DBMaker.fileDB(dbPath).closeOnJvmShutdown().fileMmapEnableIfSupported().make();

        DeviceSerializer serializer = new DeviceSerializer(this);
        HashMapMaker<String, Device> mapMaker = database.hashMap(MAP_NAME, Serializer.STRING, serializer);
        if(database.exists(MAP_NAME)){
            LOGGER.debug("Opening existing hashmap in DeviceManager constructor");
            devices = mapMaker.open();
        }else{
            LOGGER.debug("Creating new hashmap in DeviceManager constructor");
            devices = mapMaker.create();
        }
    }

    /**
     * Finds the Device with the given ID, and calls that Device's setParameter() method,
     * with the given parameterName and value
     *
     * @param deviceID ID of device to set
     * @param parameterName Name of parameter to set on that device
     * @param value Value to set that parameter to
     */
    public void setValue(String parameterName, Value value){

        //TODO Check if device actually exists, and if parameter actually exists
        devices.get(parameterName).setValue(parameterName, value);
    }
    
    /**
     * Finds the device with the given ID, and calls its getParameter() method.
     * This method might, in the future, make use of some caching if performance
     * is an issue.
     *
     * @param deviceID ID of device to find
     * @param parameterName Parameter to find on that device
     * @return That parameter from that device
     */
    public Parameter getParameter(String deviceID, String parameterName){
        //TODO Check if device and parameter actually exist
        return devices.get(deviceID).getParameter(parameterName);
    }
    
    public Value getValue(String deviceID, String parameterName){
    	return devices.get(deviceID).getValue(parameterName);
    }
}
