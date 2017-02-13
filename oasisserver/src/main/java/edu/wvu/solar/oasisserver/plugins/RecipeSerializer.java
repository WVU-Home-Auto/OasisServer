package edu.wvu.solar.oasisserver.plugins;


import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;

public class RecipeSerializer implements Serializer<Device>{

    private PluginManager pluginManager;

    public RecipeSerializer(PluginManager pluginManager){
        this.pluginManager = pluginManager;
    }

    @Override
    public void serialize(DataOutput2 dataOutput2, Device device) throws IOException {
        //TODO: Auto-generated method stub
    }

    @Override
    public Device deserialize(DataInput2 dataInput2, int available) throws IOException {
        //TODO: Auto-generated method stub
        return null;
    }
}
