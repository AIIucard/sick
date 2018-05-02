package main.htw.handler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.websocket.ClientEndpoint;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketListener;

import main.htw.messages.MessageArea;
import main.htw.parser.JsonReader;
import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.utils.ConnectionStatusType;
import main.htw.xml.Area;

/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */
@ClientEndpoint
public class RTLSConnectionHandler {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private SickMessageHandler sickMessageHandler = null;

	private static CFGPropertyManager propManager = null;

	private WebSocket websocket;
	private URI endpointURI;
	private static final String REGISTER_GEO_FENCE_MSG = "{\"topic\":\"REGISTER\",\"payload\":[\"GEOFENCING_EVENT\"]}";
	private static final String REGISTER_POSITION_MSG = "{\"topic\":\"REGISTER\",\"payload\":[\"POSITION\"]}";

	private static Object lock = new Object();
	private static RTLSConnectionHandler instance = null;

	private static URI uri;

	private static ConnectionStatusType connectionStatus;

	public static RTLSConnectionHandler getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new RTLSConnectionHandler();
					try {
						if (propManager == null) {
							propManager = CFGPropertyManager.getInstance();
						}

						String websocketString = propManager.getProperty(PropertiesKeys.WEBSOCKET_PROTOCOL)
								+ propManager.getProperty(PropertiesKeys.ZIGPOS_BASE_URL) + "/socket";
						uri = new URI(websocketString);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
					connectionStatus = ConnectionStatusType.DEAD;
					log.info("Connecting to " + uri);
					instance.createWebsocket(uri);
				}
			}
		}
		return (instance);
	}

	public void createWebsocket(URI endpointURI) {
		try {
			this.endpointURI = endpointURI;
			WebSocketFactory factory = new WebSocketFactory();
			SSLContext context;
			context = NaiveSSLContext.getInstance("TLS");
			factory.setSSLContext(context);
			factory.setVerifyHostname(false);
			websocket = factory.createSocket(endpointURI);
			if (sickMessageHandler == null) {
				log.info("Initializing Message Handler");
				sickMessageHandler = SickMessageHandler.getInstance();
			}
		} catch (Exception e) {
			log.error("Connection error! \n" + e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
	}

	public void registerGeoFence() throws WebSocketException {
		log.info("Registering to topic GEOFENCEING_EVENT");
		if (sickMessageHandler == null) {
			log.warn("sickMessagehandler is not initiliazed!");
		}
		websocket.addListener(sickMessageHandler);
		websocket.connect();
		websocket.sendText(RTLSConnectionHandler.REGISTER_GEO_FENCE_MSG);
	}

	public void registerPosition() throws WebSocketException {
		log.info("Registering to topic POSITION");
		if (sickMessageHandler == null) {
			log.warn("sickMessagehandler is not initiliazed!");
		}
		websocket.addListener(sickMessageHandler);
		websocket.connect();
		websocket.sendText(RTLSConnectionHandler.REGISTER_POSITION_MSG);
	}

	public void addArea(Area area) throws WebSocketException {
		log.info("Adding new Area to Zigpos");
		log.warn("NOT IMPLEMENTED");
	}

	public void removeArea(Area area) throws WebSocketException {
		log.info("REMOVING Area from Zigpos");
		log.warn("NOT IMPLEMENTED");
	}

	public MessageArea[] getAllAreas() throws IOException {
		log.info("Getting All Areas");
		String urlString = propManager.getProperty(PropertiesKeys.HTTP_PROTOCOL)
				+ propManager.getProperty(PropertiesKeys.ZIGPOS_BASE_URL) + "/geofencing/areas";
		URL url = new URL(urlString);

		try {
			JSONObject jsonObject = JsonReader.readJsonFromUrl(urlString);
			System.out.println(jsonObject.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void tryReconnect() throws NoSuchAlgorithmException, IOException, WebSocketException {
		log.debug("Recreating Websocket");
		WebSocketFactory factory = new WebSocketFactory();
		SSLContext context;
		context = NaiveSSLContext.getInstance("TLS");
		factory.setSSLContext(context);
		factory.setVerifyHostname(false);
		websocket = factory.createSocket(this.endpointURI);

		websocket.addListener((WebSocketListener) SickMessageHandler.getInstance());
		websocket.connect();
		// websocket.sendText(registerGeoFenceMsg);
	}

	public WebSocket getWebsocket() {
		return websocket;
	}
}