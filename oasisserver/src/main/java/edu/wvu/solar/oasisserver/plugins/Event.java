package edu.wvu.solar.oasisserver.plugins;

/**
 * Stores data for an event from a device
 * @author Syihan
 *
 */
public class Event {
	
	/** the event type */
	private String eventType;
	
	/** the device's ID */
	private String deviceID;
	
	/** Default constructor */
	public Event() {
		eventType = null;
		deviceID = null;
	}
	
	/**
	 * Constructor for Event Type
	 * @param the event type
	 * @param the device's ID
	 */
	public Event(String type, String id) {
		eventType = type;
		deviceID = id;
	}
	
	/**
	 * Gets the event type
	 * @return the event type
	 */
	public String getEventType() {
		return eventType;
	}
	
	/**
	 * Gets the device's ID
	 * @return the device's ID
	 */
	public String getDeviceID() {
		return deviceID;
	}
	
}
