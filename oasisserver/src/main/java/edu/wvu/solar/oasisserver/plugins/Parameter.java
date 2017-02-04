package edu.wvu.solar.oasisserver.plugins;

/**
 * Stores a single parameter for a device
 */
public class Parameter {

    public static enum Type{
        STRING,
        INTEGER,
        FLOAT,
        BOOLEAN,
        DEVICE,
    }

    private String name;
    private Type type;
    private Object value;
    // TODO: Add user interface options

    public Parameter(String name, Type type, Object value){
        this.name = name;
        this.type = type;
        this.value = value;
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
}
