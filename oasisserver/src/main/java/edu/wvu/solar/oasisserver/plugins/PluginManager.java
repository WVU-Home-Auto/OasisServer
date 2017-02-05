package edu.wvu.solar.oasisserver.plugins;

import edu.wvu.solar.oasisserver.plugins.Plugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;
import org.xeustechnologies.jcl.exception.JclException;
import org.xeustechnologies.jcl.proxy.CglibProxyProvider;
import org.xeustechnologies.jcl.proxy.ProxyProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.HashMap;

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
                    LOGGER.debug("Loading jar file {}", file.getName());
                    JarFile jar = new JarFile(file);

                    // Get the plugin's name and plugin class, as specified in its manifest
                    Attributes attributes = jar.getManifest().getMainAttributes();
                    String pluginClass = attributes.getValue(PLUGIN_CLASS_PROPERTY);
                    String pluginName = attributes.getValue(PLUGIN_NAME_PROPERTY);

                    jcl.add(file.getAbsolutePath());


                    this.plugins.put(pluginName, (Plugin) factory.create(jcl, pluginClass));
                    LOGGER.debug("Loaded plugin {} with class {}", pluginName, pluginClass);
                } catch (IOException e) {
                    LOGGER.error("Something went wrong while loading file {}", file.getName(), e);
                } catch (JclException e) {
                    LOGGER.error("Something went wrong while loading plugin {}", file.getName(), e);
                } catch (Error e){
                    LOGGER.error("Something completely unexpected happened while loading plugin {}.", file.getName(), e);
                }
            }
        }

        LOGGER.debug("Loaded {} plugins.", this.plugins.size());
    }

    public Plugin getPlugin(String name){
        return plugins.get(name);
    }


}
