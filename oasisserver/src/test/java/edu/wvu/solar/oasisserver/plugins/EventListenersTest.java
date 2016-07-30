package edu.wvu.solar.oasisserver.plugins;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import edu.wvu.solar.oasisserver.plugins.exceptions.InvalidEventException;
import edu.wvu.solar.oasisserver.plugins.exceptions.InvalidParametersException;

public class EventListenersTest {

	@Rule
    public TemporaryFolder folder= new TemporaryFolder();
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testAddRecipeSucceed(){
		/*
		 * Test case for when addRecipe should succeed.
		 * Expect a non-null, non-empty return
		 */
		
		EventListeners listeners = new EventListeners(folder.getRoot() + "/TempDB1.db");
		JSONObject event = new JSONObject();
		event.put(EventListeners.TYPE_LABEL, "testtype");
		JSONObject parameter = new JSONObject();
		parameter.put("name", "testParameter");
		parameter.put("value", "testValue");
		JSONArray parameters = new JSONArray();
		parameters.put(parameter);
		
		String result = listeners.addRecipe(event, "testDeviceID", parameters);
		assertNotNull(result);
		assertNotEquals(result, "");
		
		JSONArray recipes = listeners.getRecipes();
		//This is a new blank test database, so there should only be the one recipe in it
		JSONObject recipe = recipes.getJSONObject(0);
		assertEquals(recipe.getString(EventListeners.RECIPE_ID_LABEL), result);
		JSONObject newEvent = recipe.getJSONObject(EventListeners.EVENT_LABEL);
		assertEquals(newEvent.getString(EventListeners.TYPE_LABEL), "testtype");
	}
	
	@Test
	public void testAddMultipleRecipeSucceed(){
		/*
		 * Test case for when addRecipe should succeed.
		 * Specifically, adds multiple recipes of the same type
		 * Expect a non-null, non-empty return
		 */
		
		EventListeners listeners = new EventListeners(folder.getRoot() + "/TempDB2.db");
		JSONObject event = new JSONObject();
		event.put("type", "testtype");
		JSONObject parameter = new JSONObject();
		parameter.put("name", "testParameter");
		parameter.put("value", "testValue");
		JSONArray parameters = new JSONArray();
		parameters.put(parameter);
		
		String result = listeners.addRecipe(event, "testDeviceID", parameters);
		assertNotNull(result);
		assertNotEquals(result, "");
		
		String result2 = listeners.addRecipe(event, "testDeviceID", parameters);
		assertNotNull(result2);
		assertNotEquals(result2, "");
		assertNotEquals(result, result2);
	}
	
	@Test
	public void testAddRecipeNoType(){
		/*
		 * Test case for when addRecipe should fail because the
		 * supplied recipe has no type
		 */
		EventListeners listeners = new EventListeners(folder.getRoot() + "/TempDB3.db");
		JSONObject event = new JSONObject();
		JSONObject parameter = new JSONObject();
		parameter.put("name", "testParameter");
		parameter.put("value", "testValue");
		JSONArray parameters = new JSONArray();
		parameters.put(parameter);
		
		exception.expect(InvalidEventException.class);
		listeners.addRecipe(event, "testDeviceID", parameters);
	}
	
	@Test
	public void testAddRecipeNoParameters(){
		/*
		 * Test case for when addRecipe should fail because
		 * no parameters were supplied
		 */
		EventListeners listeners = new EventListeners(folder.getRoot() + "/TempDB4.db");
		JSONObject event = new JSONObject();
		event.put("type", "testtype");
		JSONArray parameters = new JSONArray();
		
		exception.expect(InvalidParametersException.class);
		listeners.addRecipe(event, "testDeviceID", parameters);
	}
	
	@Test
	public void testRemoveRecipeThatsPresent(){
		/*
		 * Test case for removing a recipe that does
		 * exist in the database
		 */
		
		EventListeners listeners = new EventListeners(folder.getRoot() + "/TempDB5.db");
		JSONObject event = new JSONObject();
		event.put(EventListeners.TYPE_LABEL, "testtype");
		JSONObject parameter = new JSONObject();
		parameter.put("name", "testParameter");
		parameter.put("value", "testValue");
		JSONArray parameters = new JSONArray();
		parameters.put(parameter);
		
		String result = listeners.addRecipe(event, "testDeviceID", parameters);
		
		listeners.removeRecipe(result, "testtype");
		
		JSONArray recipes = listeners.getRecipes();
		assertEquals(recipes.length(), 0);
	}
	
	@Test
	public void testRemoveRecipeThatsNotPresent(){
		/*
		 * Test case for removing a recipe that does NOT
		 * exist in the database
		 */
		
		EventListeners listeners = new EventListeners(folder.getRoot() + "/TempDB6.db");
		JSONObject event = new JSONObject();
		event.put(EventListeners.TYPE_LABEL, "testtype");
		JSONObject parameter = new JSONObject();
		parameter.put("name", "testParameter");
		parameter.put("value", "testValue");
		JSONArray parameters = new JSONArray();
		parameters.put(parameter);
		
		listeners.addRecipe(event, "testDeviceID", parameters);
		
		listeners.removeRecipe("some nonexistent ID", "testtype");
		
		JSONArray recipes = listeners.getRecipes();
		assertEquals(recipes.length(), 1);
	}
}
