package edu.wvu.solar.oasisserver.plugins;


/**
 * Stores a single parameter for a device
 */
public class Parameter{

    public static enum Type{
        STRING,
        INTEGER,
        FLOAT,
        BOOLEAN,
        DEVICE,
        // If you add a new data type, deal with it in compareTo()
    }

    private String name;
    private String deviceID;
    private Type type;
    // TODO: Add user interface options

    public Parameter(String name, Type type, Object value, String deviceID){
        this.name = name;
        this.type = type;
        this.deviceID = deviceID;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
    
    public String getDeviceID(){
    	return deviceID;
    }
    
}
