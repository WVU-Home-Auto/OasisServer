package edu.wvu.solar.oasisserver.plugins.api;

import org.json.JSONArray;

/**
 * This class must be implemented by any plugin that adds
 * devices to the house.
 *
 */

public abstract class Device {

	private int deviceID;
	private String description;
	private Plugin parent;
	private JSONArray parameters;
	
	public Device(int ID, String parameterList,Plugin parentPlugin){
		deviceID = ID;
		description = "";
		parameters = new JSONArray(parameterList);
		parent = parentPlugin;
	}
	public Device(int ID, String deviceDescription, String parameterList, Plugin parentPlugin){
		deviceID = ID;
		description = deviceDescription;
		parent = parentPlugin;
		parameters = new JSONArray(parameterList);
	}
	/*
	 * 
	 */
	public JSONArray getParameters(){
		return parameters;
	}
	/*
	 * 
	 */
	public void setParameters(String newParameters){
		parameters = new JSONArray(newParameters);
	}
	/*
	 * 
	 */
	public JSONArray getAvailableParameters(){
		String tmp = "";
		for(int i = 0; i<parameters.length();i++){
			boolean edit = Boolean.parseBoolean(parameters.getJSONObject(i).getString("modifiable"));
			if(edit){
				tmp = tmp+parameters.getJSONObject(i).toString();
			}
		}
		return new JSONArray(tmp);
	}
}
