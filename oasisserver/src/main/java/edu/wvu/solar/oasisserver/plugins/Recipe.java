package edu.wvu.solar.oasisserver.plugins;

import java.util.List;

public class Recipe {
	
	private String eventType;
	private List<ParameterComparison> comparisons;
	private List<Value> values;
	private DeviceManager deviceManager;
	
	public Recipe(String eventType, List<ParameterComparison> comparisons, List<Value> value, DeviceManager deviceManager){
		this.eventType = eventType;
		this.comparisons = comparisons;
		this.values = value;
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
			for(Value value : values){
				deviceManager.setValue(value.getName(), value);
			}
		}
		return;
	}
	
	
}
