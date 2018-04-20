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

import main.htw.messages.MessageGeoFence;

/**
 *
 * @author richter
 */
public class SickMessageHandler extends WebSocketAdapter {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());
	private static Object lock = new Object();
	private static SickMessageHandler instance = null;
	Session userSession = null;
	private JSONParser parser = new JSONParser();
	private RuleHandler ruleHandler;

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
		log.info("handle message: " + message);

		try {
			if (ruleHandler == null) {
				ruleHandler = RuleHandler.getInstance();
			}

			Object obj = parser.parse(message);
			JSONObject jsonObject = (JSONObject) obj;

			String topic = (String) jsonObject.get("topic");
			log.info("topic: " + topic);
			JSONObject payload = (JSONObject) jsonObject.get("payload");
			log.info("payload: " + payload);

			switch (topic) {
			case "GEOFENCING_EVENT":
				this.handleGeofencingEvent(payload);
				break;
			default:
				log.warn("unsupported topic: " + topic);
				break;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleGeofencingEvent(JSONObject payload) {
		MessageGeoFence geoFence = new MessageGeoFence();
		String eventType = "";
		try {
			eventType = (String) payload.get("eventType");
			geoFence.setAddress((String) payload.get("address"));
			geoFence.setAreaId(((Long) payload.get("areaId")).intValue());
			geoFence.setCustomName((String) payload.get("customName"));
			geoFence.setEventType(eventType);
			geoFence.setMessage((String) payload.get("message"));
			geoFence.setTimestamp((Long) payload.get("timestamp"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		geoFence.printObjectInformation();

		switch (eventType) {
		case "IN":
			log.info("IN EVENT");
			ruleHandler.handleGeofenceIn(geoFence);
			break;
		case "OUT":
			log.info("OUT EVENT");
			ruleHandler.handleGeofenceOut(geoFence);
			break;
		default:
			log.error("UNKOWN EVENT TYPE: '" + eventType + "'");
			break;
		}

	}

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
		log.info("message received!");
		handleMessage(message);
	}

	// /**
	// * register message handler
	// *
	// * @param msgHandler
	// */
	// useful if we want to have specific handlers
	// public void addMessageHandler(SickMessageHandler msgHandler) {
	// sickMessageHandler = msgHandler;
	// }

	/**
	 * Send a message.
	 *
	 * @param message
	 */
	public void sendMessage(String message) {
		log.info("Websocket received message");
	}
}
