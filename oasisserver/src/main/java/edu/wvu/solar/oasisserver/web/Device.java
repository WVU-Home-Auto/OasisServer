package edu.wvu.solar.oasisserver.web;

public class Device {
	
	private String ID;
	private String parameters;
	private boolean success;
	public Device (String ID,String parameters){
		this.ID = ID;
		this.parameters = parameters;
	}
	public String getParameters(){
		return this.parameters;
	}
	public String getID(){
		return this.ID;
	}
	public boolean getSuccess(){
		return this.success;
	}
	public void setSuccess(boolean t){
		 this.success = t;
	}
}
