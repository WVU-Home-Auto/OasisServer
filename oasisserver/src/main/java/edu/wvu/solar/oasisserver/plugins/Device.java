package edu.wvu.solar.oasisserver.plugins;

import java.util.List;

/**
 * This class should be implemented for each unique type of Device in a plugin. (This will generally
 * just be one type of Device, but it could be more)
 */
public abstract class Device {

    private String deviceID;

    /**
     * This method should be implemented to return all of the parameters of this device.
     * @see Parameter
     *
     * @return List of all parameters that this device has
     */
    public abstract List<Parameter> getParameters();

    /**
     * Should return the parameter with the given name.
     * (The underlying implementation of this would probably be a HashMap, but who am I to
     * tell you how to live your life?)
     *
     * @param name Name of the parameter to return
     * @return The parameter with a name watching the given name
     */
    public abstract Parameter getParameter(String name);

    /**
     * This method is called when the user, a recipe, another device, or anything else wants to
     * change a parameter on this device. The implementation of this method should communicate over
     * a network, serial port, or something else, to tell the physical device about the
     * parameter change.
     *
     * @param name Name of parameter to change
     * @param value Value to which to set the parameter
     */
    public abstract void setParameter(String name, Object value);

    /**
     * This method returns the unique, randomly-assigned ID of this device. This ID is assigned by
     * the system, and so this method cannot be overridden to provide a different ID.
     *
     * @return Unique ID assigned by the system
     */
    public final String getDeviceID(){
        return deviceID;
    }

    /**
     * This method will be called exactly once by the system on startup to set this Device's ID
     *
     * @param pluginID Unique ID assigned to this Device
     */
    protected final void setDeviceID(String pluginID){
        this.deviceID = pluginID;
    }
}
