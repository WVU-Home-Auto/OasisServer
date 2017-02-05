package edu.wvu.solar.oasisserver.plugins;

import java.util.List;

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

    public abstract Device loadDevice(List<Parameter> parameters);
}
