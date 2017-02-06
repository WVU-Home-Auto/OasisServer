package edu.wvu.solar.oasisserver.plugins;


/**
 * Stores a single parameter for a device
 */
public class Parameter implements Comparable<Parameter>{

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
    private Object value;
    // TODO: Add user interface options

    public Parameter(String name, Type type, Object value, String deviceID){
        this.name = name;
        this.type = type;
        this.value = value;
        this.deviceID = deviceID;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
    
    public String getDeviceID(){
    	return deviceID;
    }
    
    public void setValue(Object newValue){
    	this.value = newValue;
    }

    public int compareTo(Parameter other) throws ClassCastException{
        if(this.type != other.type){
            throw new ClassCastException("These two Parameters are not of the same type. This is " + this.type + ", while other is " + other.type);
        }else{
            switch(type){
                case BOOLEAN:
                    boolean myBool = (Boolean) this.value;
                    boolean otherBool = (Boolean) other.value;
                    if(myBool == otherBool){
                        return 0;
                    }else{
                        return myBool ? 1 : -1;
                    }
                case STRING:
                    String myString = (String) this.value;
                    String otherString = (String) other.value;
                    return myString.compareTo(otherString);
                case INTEGER:
                    Integer myInteger = (Integer) this.value;
                    Integer otherInteger = (Integer) other.value;
                    return myInteger.compareTo(otherInteger);
                case FLOAT:
                    Float myFloat = (Float) this.value;
                    Float otherFloat = (Float) other.value;
                    return myFloat.compareTo(otherFloat);
                case DEVICE:
                    String myDeviceID = (String) this.value;
                    String otherDeviceID = (String) other.value;
                    return myDeviceID.compareTo(otherDeviceID);
                default:
                    throw new RuntimeException("You added a new Type to Parameter.Type and didn't handle it in CompareTo()");
            }
        }
    }
}
