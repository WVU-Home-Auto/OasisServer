package edu.wvu.solar.oasisserver.plugins;

import edu.wvu.solar.oasisserver.plugins.api.Plugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;
import org.xeustechnologies.jcl.proxy.CglibProxyProvider;
import org.xeustechnologies.jcl.proxy.ProxyProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.jar.JarFile;

/**
 * This app will be responsible for managing all plugins on the server,
 * including loading them at startup and anything else important.
 *
 */
public class PluginManager {

    public static final String PLUGIN_CLASS_PROPERTY = "PluginClass";
    public static final String PLUGIN_NAME_PROPERTY = "PluginName";
    private static final Logger LOGGER = LogManager.getLogger(PluginManager.class);

    private HashMap<String, Plugin> plugins;

    public PluginManager(String pluginsDirectory){
        plugins = new HashMap<String, Plugin>();

        JarClassLoader jcl = new JarClassLoader();

        // Set default to cglib (from version 2.2.1)
        ProxyProviderFactory.setDefaultProxyProvider( new CglibProxyProvider() );

        //Create a factory of castable objects/proxies
        JclObjectFactory factory = JclObjectFactory.getInstance(true);

        File pluginsDir = new File(pluginsDirectory);
        for(File file : pluginsDir.listFiles()){
            if(file.isFile() && file.getName().endsWith(".jar")){
                try {
                    JarFile jar = new JarFile(file);
                    String pluginClass = (String) jar.getManifest().getAttributes(PLUGIN_CLASS_PROPERTY).values().iterator().next();
                    String pluginName = (String) jar.getManifest().getAttributes(PLUGIN_NAME_PROPERTY).values().iterator().next();

                    jcl.add(file);
                    LOGGER.debug("Loaded plugin {} with class {}", pluginName, pluginClass);

                    this.plugins.put(pluginName, (Plugin) factory.create(jcl, pluginClass));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Collection<Plugin> getPlugins(){
        return plugins.values();
    }

}
