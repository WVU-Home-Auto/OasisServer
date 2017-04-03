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
            // Etc.
        }
	    return -1;
	}
}