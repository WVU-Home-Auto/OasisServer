package edu.wvu.solar.oasisserver.plugins;

import java.util.List;

public abstract class Plugin {

    private String pluginID;

    public abstract String getDisplayName();

    public final String getPluginID(){
        return pluginID;
    }

    protected final void setPluginID(String pluginID){
        this.pluginID = pluginID;
    }

    public abstract Device loadDevice(List<Parameter> parameters);
}
