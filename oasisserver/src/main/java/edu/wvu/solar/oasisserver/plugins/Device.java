package edu.wvu.solar.oasisserver.plugins;

import java.util.List;

public abstract class Device {

    private String deviceID;

    public abstract List<Parameter> getParameters();
    public abstract Parameter getParameter(String name);
    public abstract void setParameter(String name, Object value);

    public final String getDeviceID(){
        return deviceID;
    }

    protected final void setDeviceID(String pluginID){
        this.deviceID = pluginID;
    }
}
