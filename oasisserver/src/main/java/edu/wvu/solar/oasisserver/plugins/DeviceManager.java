package edu.wvu.solar.oasisserver.plugins;

import org.json.JSONArray;

/**
 * This class will maintain a list of all devices in the house,
 * and facilitate communication between them, including events
 * and parameters.
 *
 */
public class DeviceManager {

    /**
     * @param deviceID ID of deivce to check parameters of
     * @return The result of calling getParameters() on the specified device
     */
    public JSONArray getParameters(String deviceID){
        // TODO Shockingly, this should not always return null
        return null;
    }

    /**
     * Simply calls setParameters() on the specified device
     * @param deviceID ID of device of which to set parameters
     * @param parameters Parameters to be set. This could only contain those parameters you wish to change,
     *                   or could also include the other parameters that don't change.
     */
    public void setParameters(String deviceID, JSONArray parameters){

    }

}
