package edu.wvu.solar.oasisserver.demo;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonDemo {

	public static void main(String[] args){
		
		/*
		 * [
		 * 	   {
		 * 			"name":"on",
		 * 			"type":"boolean",
		 * 			"editable":true,
		 * 			"value":true
		 * 		},{
		 * 			"name":"color",
		 * 			"type":"color",
		 * 			"editable":true,
		 * 			"value":"FF0000"
		 * 		}
		 * ]
		 */
		//This string is the same thing as the above JSON, just with all the quotes escaped
		//with backslashes, and all the newlines and indentation removed.
		String demoJson = "[{\"name\":\"on\", \"type\":\"boolean\", \"editable\":true, \"value\":true }," 
			+ "{ \"name\":\"color\", \"type\":\"color\", \"editable\":true, \"value\":\"FF0000\" } ]";
		
		JSONArray jsonArray = new JSONArray(demoJson); //This takes that text and parses it into in-memory objects
		System.out.println("JSON Array:");
		System.out.println(jsonArray.toString(3)); //The number argument of toString() makes it indent everything all fancy,
											 //like in the above multi-line comment. Try changing or removing it.

		JSONObject obj1 = jsonArray.getJSONObject(0);
		System.out.println("\n\nFirst JSON Object:");
		System.out.println(obj1.toString(3));
		
		String name = obj1.getString("name"); //Returns "on" as a normal Java String
		System.out.println("\nName: " + name);
		boolean editable = obj1.getBoolean("editable"); //Returns true as a normal Java boolean
		System.out.println("Editable: " + editable);
		
		//Here's a problem with JSON. Poorly formatted JSON objects cause errors.
		//Which means we'll need quite a lot of error checking and try-catch blocks
		try{
			String nothing = obj1.getString("Some key that doesn't exist");
		}catch (Exception e){
			System.out.println("Error! " + e.getMessage());
		}
		
		/*
		 * The benefit, though, is that every parameter, no matter the type, can be handled in the exact
		 * same way. We wouldn't have to worry about casting, or parsing strings, or any of that
		 * nonsense. The JSONObject and JSONArray classes handle all of that for us.
		 * 
		 * Another benefit is that every parameter is optional. So if we want to just change the "on"
		 * parameter of a lightbulb, just create this JSONArray and pass it to the setParameters method.
		 */
		
		JSONObject onParameter = new JSONObject();
		onParameter.put("name", "on");
		onParameter.put("value", false);
		JSONArray arr2 = new JSONArray();
		arr2.put(onParameter);
		//This results in the following JSON
		/*
		 * 	[
		 * 		{
		 *			"name":"on",
		 *			"value":true 
		 * 		}
		 * 	]
		 */
		//But we don't really have to worry about the formatting, since JSONObject and JSONArray
		//handle it all for us.
		System.out.println("\n\nNew Array:\n" + arr2.toString(3));
		
		
		
		//Note: The actual structure of all this JSON is up for discussion. For example,
		//instead of the above mess, changing parameters could be as easy as passing this JSONObject to
		//the setParameters method:
		/*
		 * {	
		 * 		"on":true
		 * }
		 */
		JSONObject simplerParameter = new JSONObject();
		simplerParameter.put("on", true);
		
		//Or, to edit multiple parameters at once:
		/*
		 * {
		 * 		"on":true,
		 * 		"color":"FF00FF"
		 * }
		 */
		JSONObject simplerMultipleParameters = new JSONObject();
		simplerMultipleParameters.put("on", true);
		simplerMultipleParameters.put("color", "FF00FF");
		
	}
}
