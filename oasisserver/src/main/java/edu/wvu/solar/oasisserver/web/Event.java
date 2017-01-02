package edu.wvu.solar.oasisserver.web;

public class Event {
	private String type;
	private String parameters;
	
	public Event(String type,String parameters){
		this.type = type;
		this.parameters = parameters;
	}
	public String getParameters(){
		return this.parameters;
	}
	public String getType(){
		return this.type;
	}
}