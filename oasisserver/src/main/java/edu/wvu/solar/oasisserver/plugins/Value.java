package edu.wvu.solar.oasisserver.plugins;

/**
* Value class to replace parameter pretty much.
*/
public class Value implements Comparable<Value>{
	
	private Parameter.Type type;
	private String name;
	private String deviceID;
    private Object value;

	public Value(Parameter parameter, Object value){
		this.value = value;
        this.type = parameter.getType();
        this.name = parameter.getName();
	}

	public Object getValue(){
	    return this.value;
    }
	
	public String getName(){
		return name;
	}

	public int compareTo(Value other) {
	    switch(type){
            case INTEGER:
                Integer otherInt = (Integer) other.getValue();
                Integer myInt = (Integer) this.value;
                return myInt.compareTo(otherInt);
            case BOOLEAN:
            	Boolean otherBool = (Boolean) other.getValue();
            	Boolean myBool = (Boolean) this.value;
            	return myBool.compareTo(otherBool);
            case STRING:
            	String otherStr = (String) other.getValue();
            	String myStr = (String) this.value;
            	return myStr.compareTo(otherStr);
            case FLOAT:
            	Float otherDbl = (Float) other.getValue();
            	Float myDbl = (Float) this.value;
            	return myDbl.compareTo(otherDbl);
            case DEVICE:
            	String otherDevice = (String) other.getValue();
            	String myDevice = (String) this.value;
            	return myDevice.compareTo(otherDevice);
                // Etc.
        }
	    return -1;
	}
}