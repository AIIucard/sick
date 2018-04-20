/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.htw.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author stefan.riedel
 */
public class MessageGeoFence {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private String message;
	private long timestamp;
	private String eventType;
	private int areasId;
	private String address;
	private String customName;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		log.debug("Message '" + message + "' set.");
		this.message = message;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		log.debug("Timestamp '" + timestamp + "' set.");
		this.timestamp = timestamp;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		log.debug("EventType '" + eventType + "' set.");
		this.eventType = eventType;
	}

	public int getAreasId() {
		return areasId;
	}

	public void setAreaId(int areaId) {
		log.debug("AreaId '" + areaId + "' set.");
		this.areasId = areaId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		log.debug("Address '" + address + "' set.");
		this.address = address;
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		log.debug("CustomName '" + customName + "' set.");
		this.customName = customName;
	}

	public void printObjectInformation() {
		log.info("GF Event    : " + getEventType());
		log.info("GF Adress   : " + getAddress());
		log.info("GF Area Id  : " + getAreasId());
		log.info("GF Cus-Name : " + getCustomName());
		log.info("GF Timestamp: " + getTimestamp());
		log.info("GF Message  : " + getMessage());
	}
}
