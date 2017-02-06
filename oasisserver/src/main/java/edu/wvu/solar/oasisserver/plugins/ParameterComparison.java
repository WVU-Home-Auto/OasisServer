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
    Parameter testValue;

    public ParameterComparison(DeviceManager deviceManager, String deviceID, String parameterName,
                               Comparison comparison, Parameter testValue){
        this.deviceID = deviceID;
        this.parameterName = parameterName;
        this.comparison = comparison;
        this.testValue = testValue;
        this.deviceManager = deviceManager;
    }

	public boolean matches(){
	    Parameter actualParam = deviceManager.getParameter(deviceID, this.testValue.getName());
    	switch(comparison){
    		case GREATER: 
    			return actualParam.compareTo(testValue) > 0;
    		case GREATER_OR_EQUAL:
    			return actualParam.compareTo(testValue) >= 0;
    		case EQUAL:
    			return actualParam.compareTo(testValue) == 0;
    		case NOT_EQUAL:
    			return actualParam.compareTo(testValue) != 0;
    		case LESS_OR_EQUAL:
    			return actualParam.compareTo(testValue) <= 0;
    		case LESS:
    			return actualParam.compareTo(testValue) < 0;
    		default:
    			return false;// if here, some done messed up
    	}
    }
}
