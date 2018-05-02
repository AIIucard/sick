package main.htw.handler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.websocket.ClientEndpoint;

import org.json.JSONException;
import org.json.simple.JSONObject;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketListener;

import main.htw.messages.MessageArea;
import main.htw.parser.JsonReader;
import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.xml.Area;

/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */
@ClientEndpoint
public class RTLSConnectionHandler extends SickConnectionHandler {

	private SickMessageHandler sickMessageHandler = null;

	private WebSocket websocket;
	private static final String REGISTER_GEO_FENCE_MSG = "{\"topic\":\"REGISTER\",\"payload\":[\"GEOFENCING_EVENT\"]}";
	private static final String REGISTER_POSITION_MSG = "{\"topic\":\"REGISTER\",\"payload\":[\"POSITION\"]}";

	private static Object lock = new Object();
	private static RTLSConnectionHandler instance = null;

	private static URI uri;

	public static RTLSConnectionHandler getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new RTLSConnectionHandler();
					try {
						if (propManager == null) {
							try {
								propManager = CFGPropertyManager.getInstance();
								String websocketString = propManager.getProperty(PropertiesKeys.WEBSOCKET_PROTOCOL)
										+ propManager.getProperty(PropertiesKeys.ZIGPOS_BASE_URL) + "/socket";
								uri = new URI(websocketString);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (URISyntaxException e) {
						// TODO: Log
						e.printStackTrace();
					}
					setStatusPending();
					initializeConnection();
				}
			}
		}
		return (instance);
	}

	private static void initializeConnection() {
		log.info("Connecting to RTLS at" + uri + "...");
		instance.createWebsocket(uri);
	}

	public void createWebsocket(URI endpointURI) {
		WebSocketFactory factory = new WebSocketFactory();
		SSLContext context;
		try {
			context = NaiveSSLContext.getInstance("TLS");
			factory.setSSLContext(context);
			factory.setVerifyHostname(false);
			websocket = factory.createSocket(endpointURI);
			if (sickMessageHandler == null) {
				log.info("Initializing Message Handler");
				sickMessageHandler = SickMessageHandler.getInstance();
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatusError();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatusError();
		}
	}

	public void registerGeoFence() {
		log.info("Registering to topic GEOFENCEING_EVENT");
		if (sickMessageHandler == null) {
			log.warn("sickMessagehandler is not initiliazed!");
		}
		websocket.addListener(sickMessageHandler);
		try {
			websocket.connect();
			websocket.sendText(RTLSConnectionHandler.REGISTER_GEO_FENCE_MSG);
		} catch (WebSocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatusError();
		}
	}

	public void registerPosition() {
		log.info("Registering to topic POSITION");
		if (sickMessageHandler == null) {
			log.warn("sickMessagehandler is not initiliazed!");
		}
		websocket.addListener(sickMessageHandler);
		try {
			websocket.connect();
			websocket.sendText(RTLSConnectionHandler.REGISTER_POSITION_MSG);
		} catch (WebSocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatusError();
		}
	}

	public void addArea(Area area) {
		log.info("Adding new Area to Zigpos");
		log.warn("NOT IMPLEMENTED");
	}

	public void removeArea(Area area) {
		log.info("REMOVING Area from Zigpos");
		log.warn("NOT IMPLEMENTED");
	}

	public MessageArea[] getAllAreas() {
		log.info("Getting All Areas");
		String urlString = propManager.getProperty(PropertiesKeys.HTTP_PROTOCOL)
				+ propManager.getProperty(PropertiesKeys.ZIGPOS_BASE_URL) + "/geofencing/areas";
		try {
			JSONObject jsonObject = JsonReader.readJsonFromUrl(urlString);
			System.out.println(jsonObject.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatusError();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatusError();
		}

		return null;
	}

	public void tryReconnect() {
		log.debug("Recreating Websocket");
		WebSocketFactory factory = new WebSocketFactory();
		SSLContext context;
		try {
			context = NaiveSSLContext.getInstance("TLS");
			factory.setSSLContext(context);
			factory.setVerifyHostname(false);
			websocket = factory.createSocket(uri);
			websocket.addListener((WebSocketListener) SickMessageHandler.getInstance());
			websocket.connect();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatusError();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatusError();
		} catch (WebSocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatusError();
		}
	}

	public WebSocket getWebsocket() {
		return websocket;
	}
}