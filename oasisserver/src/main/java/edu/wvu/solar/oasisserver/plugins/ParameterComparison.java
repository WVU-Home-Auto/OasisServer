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
    Value testValue;

    public ParameterComparison(DeviceManager deviceManager, String deviceID, String parameterName,
                               Comparison comparison, Value testValue){
        this.deviceID = deviceID;
        this.parameterName = parameterName;
        this.comparison = comparison;
        this.testValue = testValue;
        this.deviceManager = deviceManager;
    }

	public boolean matches(){
	    Value actualValue = deviceManager.getValue(deviceID, this.testValue.getName());
    	switch(comparison){
    		case GREATER:
    			return actualValue.compareTo(testValue) > 0;
    		case GREATER_OR_EQUAL:
    			return actualValue.compareTo(testValue) >= 0;
    		case EQUAL:
    			return actualValue.compareTo(testValue) == 0;
    		case NOT_EQUAL:
    			return actualValue.compareTo(testValue) != 0;
    		case LESS_OR_EQUAL:
    			return actualValue.compareTo(testValue) <= 0;
    		case LESS:
    			return actualValue.compareTo(testValue) < 0;
    		default:
    			return false;// if here, some done messed up
    	}
    }
}
