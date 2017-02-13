package edu.wvu.solar.oasisserver.plugins;


import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;
import java.util.List;

public class RecipeSerializer implements Serializer<List<Recipe>>{
    
    public RecipeSerializer(){
        // TODO: Auto-generated stub
    }

    @Override
    public void serialize(DataOutput2 dataOutput2, List<Recipe> recipes) throws IOException {
        //TODO: Auto-generated method stub
    }

    @Override
    public List<Recipe> deserialize(DataInput2 dataInput2, int available) throws IOException {
        //TODO: Auto-generated method stub
        return null;
    }
}
