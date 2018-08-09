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
import main.htw.manager.AreaManager;

/**
 * RobotHandler is a singleton, so the instantiation is restricted to one
 * object. The RobotHandler coordinates the following actions:
 * <ul>
 * <li>Handle the incoming messages
 * <li>Handle the {@link WebSocketAdapter}
 * </ul>
 */
public class SickMessageHandler extends WebSocketAdapter {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());
	private static Object lock = new Object();
	private static SickMessageHandler instance = null;
	Session userSession = null;

	/**
	 * Use <code>getInstance</code> method!
	 * 
	 * @deprecated
	 */
	private SickMessageHandler() {
		// Use getInstance
	}

	/**
	 * Realizes the singleton pattern with synchronized(lock), ensures that just one
	 * class can instantiate this class in one specific moment. If there is already
	 * an instance of this class, the method returns a reference.
	 *
	 * @return the new or referenced instance of this class.
	 */
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

	/**
	 * Parses the given message object and handles it depending on the specific
	 * topic. <br>
	 * Currently only messages with the topic <code>GEOFENCING_EVENT</code> will be
	 * handled. All other messages will be ignored by the application.
	 * <p>
	 * If the message cannot be parsed by the JSONParser an exception will be
	 * logged.
	 * 
	 * @param message
	 *            The message which will be parsed and handled depending on the
	 *            message topic.
	 */
	public void handleMessage(String message) {
		log.debug("Handle message: " + message);

		JSONParser parser = new JSONParser();
		try {
			Object msgObj = parser.parse(message);
			JSONObject jsonObject = (JSONObject) msgObj;
			String topic = (String) jsonObject.get("topic");

			switch (topic) {

			case "GEOFENCING_EVENT":
				JSONObject payload = (JSONObject) jsonObject.get("payload");
				if (AreaManager.checkIfActiveAreaExistsByID(Integer.parseInt(String.valueOf(payload.get("areaId"))))) {
					ApplicationManager.getInstance().handleGeofenceEvent(payload);
				}
				break;

			default:
				log.warn("Unsupported topic: " + topic + "! Message not handled!");
				break;

			}

		} catch (ParseException e) {
			log.error("Cannot parse message! A ParseException occured: " + e.getLocalizedMessage());
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
		log.debug("Sick MessageHandler received a message!");
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
