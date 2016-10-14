package edu.wvu.solar.oasisserver.plugins;

import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONArray;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

// TODO I just copied the JSONArraySerializer, this might need changed.
public class JSONObjectSerializer implements Serializer<JSONObject>{

	public int compare(JSONArray arr1, JSONArray arr2) {
		return arr1.toString().compareTo(arr2.toString());
	}

	public JSONObject deserialize(DataInput2 arg0, int arg1) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void serialize(DataOutput2 arg0, JSONObject arg1) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
