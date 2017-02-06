package edu.wvu.solar.oasisserver.plugins;

import java.util.List;

/**
 * This class should be implemented exactly one by each plugin.
 * The manifest of that plugin's JAR file should contain an entry indicating where the
 * implementation of this class is:
 * PluginClass: com.example.PluginImplementation
 * (Obviously, with the actual fully-qualified name of your implementation of this class)
 */
public abstract class Plugin {

    /**
     * This method should return a constant value that represents a nice, human-readable
     * name for this plugin. This name will be shown to the user, but will not be used to
     * uniquely reference this plugin in code. Thus, it need not be unique.
     *
     * @return A human-readable name for this plugin
     */
    public abstract String getDisplayName();

    /**
     * This method should return a constant value that represents a unique name for this plugin.
     * This name will be used to uniquely reference a plugin, but will not be shown to the user.
     * Thus, it is recommended that you follow java package naming conventions to ensure uniqueness.
     * That is, use the reverse of a domain name that you own.
     * E.g., com.example.sampleplugin
     *
     * @return Unique, constant name for this plugin
     */
    public abstract String getPluginName();

    /**
     * This method will be called once at startup for each Device that was saved from the last
     * time the system was on. Because our system can't construct an abstract class, your plugin
     * has to do it. For this reason, it's important to store all of the information needed to
     * connect to your device in Parameters.
     *
     * @param parameters The list of all parameters of this Device, as provided at shutdown
     *                   the last time the system was on
     * @return An instance of your Plugin's implementation of the Device class
     */
    public abstract Device loadDevice(List<Parameter> parameters);
}
