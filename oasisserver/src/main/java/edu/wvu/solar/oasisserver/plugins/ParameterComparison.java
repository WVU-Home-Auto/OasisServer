package edu.wvu.solar.oasisserver.plugins;

public class ParameterComparison {

    public static enum Comparison {
        GREATER,
        GREATER_OR_EQUAL,
        EQUAL,
        NOT_EQUAL,
        LESS_OR_EQUAL,
        LESS,
    }

    DeviceManager deviceManager;

    String deviceID;
    String parameterName;
    Comparison comparison;
    Object testValue;

    public ParameterComparison(DeviceManager deviceManager, String deviceID, String parameterName,
                               Comparison comparison, Object testValue){
        this.deviceID = deviceID;
        this.parameterName = parameterName;
        this.comparison = comparison;
        this.testValue = testValue;
        this.deviceManager = deviceManager;
    }

    public boolean matches(Parameter actualParam){
        Object actualValue = actualParam.getValue();
        if(actualValue instanceof Comparable     && testValue instanceof Comparable){
            int diff = ((Comparable) actualValue).compareTo()
        }
    }
}
