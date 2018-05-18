/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.htw.handler;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;

import main.htw.ApplicationManager;

/**
 *
 * @author richter
 */
public class SickMessageHandler extends WebSocketAdapter {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());
	private static Object lock = new Object();
	private static SickMessageHandler instance = null;
	Session userSession = null;

	public static SickMessageHandler getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new SickMessageHandler();
				}
			}
			log.info("Message handler initialized");
		}
		return (instance);
	}

	public void handleMessage(String message) {
		log.info("Handle message: " + message);

		JSONParser parser = new JSONParser();
		try {
			Object msgObj = parser.parse(message);
			JSONObject jsonObject = (JSONObject) msgObj;
			String topic = (String) jsonObject.get("topic");

			switch (topic) {

			case "GEOFENCING_EVENT":
				JSONObject payload = (JSONObject) jsonObject.get("payload");
				String eventType = (String) payload.get("eventType");

				switch (eventType) {
				case "IN":
					ApplicationManager.getInstance().handleINEvent(payload);
					break;
				case "OUT":
					ApplicationManager.getInstance().handleOUTINEvent(payload);
					break;
				}

			default:
				log.warn("Unsupported topic: " + topic + "! Message not handled!");
				break;

			}

		} catch (ParseException e) {
			log.error("Cannot parse message! A ParseException occured: " + e.getLocalizedMessage());
		}
	}

	// private void handleGeofencingEvent(JSONObject payload) {
	// MessageGeoFence geoFence = new MessageGeoFence();
	// String eventType = "";
	// try {
	// eventType = (String) payload.get("eventType");
	// geoFence.setAddress((String) payload.get("address"));
	// geoFence.setAreaId(((Long) payload.get("areaId")).intValue());
	// geoFence.setCustomName((String) payload.get("customName"));
	// geoFence.setEventType(eventType);
	// geoFence.setMessage((String) payload.get("message"));
	// geoFence.setTimestamp((Long) payload.get("timestamp"));
	//
	// geoFence.printObjectInformation();
	//
	// Badge badge = SickUtils.getBadgeByAddress(geoFence.getAddress());
	// if (badge == null) {
	// throw new Exception("Badge not found!\nBadge Address: " +
	// geoFence.getAddress());
	// }
	//
	// log.info("badge: " + badge.getAddress() + ", role: " + badge.getRole());
	//
	// switch (eventType) {
	// case "IN":
	// log.info("IN EVENT");
	// dmnHandler.handleGeofenceIn(geoFence);
	// break;
	// case "OUT":
	// log.info("OUT EVENT");
	// dmnHandler.handleGeofenceOut(geoFence);
	// break;
	// default:
	// log.error("UNKOWN EVENT TYPE: '" + eventType + "'");
	// break;
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	/**
	 * Callback hook for Connection open events.
	 *
	 * @param userSession
	 *            the userSession which is opened.
	 */
	@OnOpen
	public void onOpen(Session userSession) {
		log.info("opening websocket");
		this.userSession = userSession;
	}

	/**
	 * Callback hook for Connection close events.
	 *
	 * @param userSession
	 *            the userSession which is getting closed.
	 * @param reason
	 *            the reason for connection close
	 */
	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		log.info("closing websocket");
		this.userSession = null;
	}

	/**
	 * Callback hook for Message Events. This method will be invoked when a client
	 * send a message.
	 *
	 * @param message
	 *            The text message
	 */
	@Override
	public void onTextMessage(WebSocket ws, String message) {
		log.info("Sick MessageHandler received a message!");
		handleMessage(message);
	}

	/**
	 * Send a message.
	 *
	 * @param message
	 */
	public void sendMessage(String message) {
		log.info("Websocket received message");
	}
}
