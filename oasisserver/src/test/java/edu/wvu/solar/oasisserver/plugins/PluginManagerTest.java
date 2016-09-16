package edu.wvu.solar.oasisserver.plugins;

import edu.wvu.solar.oasisserver.plugins.api.Plugin;
import org.junit.Test;

public class PluginManagerTest {

    @Test
    public void testTest(){
        PluginManager test = new PluginManager("/Users/Timmy/Desktop/plugins/");
        for(Plugin plugin : test.getPlugins()){
            System.out.println(plugin.getName());
        }
    }

}
