package edu.wvu.solar.oasisserver.plugins;

import java.util.List;

public class Recipe {
	
	private String eventType;
	private List<ParameterComparison> comparisons;
	private List<Parameter> parameters;
	private DeviceManager deviceManager;
	
	public Recipe(String eventType, List<ParameterComparison> comparisons, List<Parameter> parameters, DeviceManager deviceManager){
		this.eventType = eventType;
		this.comparisons = comparisons;
		this.parameters = parameters;
		this.deviceManager = deviceManager;
	}

	public boolean checkComparisons(){
		boolean checkCompFlag = true;
		for(ParameterComparison comparison : comparisons){
			if(comparison.matches()){
				checkCompFlag = true;
			}
			else{
				return false; //once a false is found, then we must break
			}
		}
		return checkCompFlag;
	}
	
	public void setParameters(){
		if(checkComparisons()){
			for(Parameter parameter : parameters){
				deviceManager.setParameter(parameter, parameter.getValue());
			}
		}
		return;
	}
	
	
}
