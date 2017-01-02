package edu.wvu.solar.oasisserver.web;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import edu.wvu.solar.oasisserver.plugins.DeviceManager;
import edu.wvu.solar.oasisserver.plugins.EventManager;

/**
 * This class will contain all REST calls that an app or
 * web client will make to the server.
 *
 */
@RestController
public class AppRestController {

	DeviceManager dm = new DeviceManager("devices.db");
	EventManager em = new EventManager("events.db");

	@RequestMapping("/register")
	    public Account account(@RequestParam(value="username") String username,@RequestParam(value="password") String password,@RequestParam(value="email") String email) {
			
	        return new Account(username,email,password);
	 }
	 @RequestMapping("/password")
	    public Account password(@RequestParam(value="username") String username,@RequestParam(value="password") String password) {
	        return new Account(username, password);
	    }
	@RequestMapping("/addDevice")
	public Device addDevice(@RequestParam(value="ID") String ID,@RequestParam(value="parameters") String parameters) {
		JSONObject obj = new JSONObject();
		obj.put("id", ID);
		obj.put("parameters", parameters);
		boolean t = dm.registerDevice(obj);
		Device d = new Device(ID,parameters);
		d.setSuccess(t);
		return d;
	}
	@RequestMapping(value="/listDevices")
	@ResponseBody
	public String listDevices() {
		JSONArray d = dm.getDevices();
		return d.toString();
	}
	@RequestMapping("/addEvent")
	public Event addEvent(@RequestParam(value="type") String type,@RequestParam(value="parameters") String parameters) {
		return new Event(type,parameters);
	}
	@RequestMapping(value="/listEvents")
	@ResponseBody
	public String listEvents() {
		JSONArray d = em.getEvents();
		return d.toString();
	}
}
