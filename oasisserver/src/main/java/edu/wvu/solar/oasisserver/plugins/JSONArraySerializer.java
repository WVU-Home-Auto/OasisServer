package edu.wvu.solar.oasisserver.plugins;

import java.io.IOException;

import org.json.JSONArray;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

public class JSONArraySerializer implements Serializer<JSONArray>{

	public int compare(JSONArray arr1, JSONArray arr2) {
		return arr1.toString().compareTo(arr2.toString());
	}

	public JSONArray deserialize(DataInput2 input, int available) throws IOException {
		String json = input.readUTF();
		return new JSONArray(json);
	}

	public void serialize(DataOutput2 output, JSONArray array) throws IOException {
		String json = array.toString();
		output.writeUTF(json);
	}

}
