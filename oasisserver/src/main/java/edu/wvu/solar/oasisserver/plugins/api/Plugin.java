package edu.wvu.solar.oasisserver.plugins.api;

import org.json.JSONArray;

import edu.wvu.solar.oasisserver.plugins.PluginManager;

/**
 * This class must be implemented at least once
 * by each plugin on the server. It is the starting point
 * for every plugin.
 *
 */
public abstract class Plugin {

	private String name;
	
	public Plugin(String plugName){
		name = plugName;
	}

	public Plugin(){

    }
	
	public abstract void initialize(PluginManager p, JSONArray j);
	
	public String getName(){
		return name;
	}
}
