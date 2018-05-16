package main.htw.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.websocket.ClientEndpoint;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketListener;

import main.htw.database.SickDatabase;
import main.htw.datamodell.ActiveBadge;
import main.htw.datamodell.RoleType;
import main.htw.datamodell.VirtualFence;
import main.htw.parser.JsonReader;
import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.xml.Area;
import main.htw.xml.Badge;
import main.htw.xml.BadgeList;

/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */
@ClientEndpoint
public class RTLSHandler extends SickHandler {

	private SickMessageHandler sickMessageHandler = null;

	private WebSocket websocket;
	private static final String REGISTER_GEO_FENCE_MSG = "{\"topic\":\"REGISTER\",\"payload\":[\"GEOFENCING_EVENT\"]}";
	private static final String REGISTER_POSITION_MSG = "{\"topic\":\"REGISTER\",\"payload\":[\"POSITION\"]}";

	private static Object lock = new Object();
	private static RTLSHandler instance = null;

	private static URI uri;

	public static RTLSHandler getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new RTLSHandler();
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
				}
			}
		}
		return (instance);
	}

	public void initializeConnection() throws Exception {
		log.info("Connecting to RTLS at" + uri + "...");
		WebSocketFactory factory = new WebSocketFactory();
		SSLContext context;
		context = NaiveSSLContext.getInstance("TLS");
		factory.setSSLContext(context);
		factory.setVerifyHostname(false);
		websocket = factory.createSocket(uri);
		if (sickMessageHandler == null) {
			log.info("Initializing Message Handler");
			sickMessageHandler = SickMessageHandler.getInstance();
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
			websocket.sendText(RTLSHandler.REGISTER_GEO_FENCE_MSG);
		} catch (WebSocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			websocket.sendText(RTLSHandler.REGISTER_POSITION_MSG);
		} catch (WebSocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public List<VirtualFence> getAllAreas() {
		Long sickLayer = Long.parseLong(propManager.getProperty(PropertiesKeys.ZIGPOS_SICK_LAYER));
		String urlString = propManager.getProperty(PropertiesKeys.HTTPS_PROTOCOL)
				+ propManager.getProperty(PropertiesKeys.ZIGPOS_BASE_URL) + "/geofencing/areas";
		JSONArray jsonArray;

		log.info("Getting All Areas");

		try {
			jsonArray = JsonReader.readJsonArrayFromUrl(urlString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		List<VirtualFence> areas = new ArrayList();

		for (Object o : jsonArray) {
			JSONObject jArea = (JSONObject) o;

			Long id = (Long) jArea.get("id");
			Long layer = (Long) jArea.get("layer");
			String name = (String) jArea.get("name");
			JSONObject shape = (JSONObject) jArea.get("shape");
			JSONArray coordinates = (JSONArray) shape.get("coordinates");

			if (layer == sickLayer) {
				log.info("OUR AREA DETECTED!!! " + name);
				VirtualFence area = new VirtualFence(id, layer, name);
				areas.add(area);
			}
		}

		return areas;
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WebSocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getActiveBadges() {
		SickDatabase sickDatabase = SickDatabase.getInstance();
		// get JSON with badges
		// WORKAROUND: read from sample file
		JSONArray jsonArray;
		try {
			// read JSON from UR
			String urlString = propManager.getProperty(PropertiesKeys.HTTPS_PROTOCOL)
					+ propManager.getProperty(PropertiesKeys.ZIGPOS_BASE_URL) + "/devices";
			jsonArray = JsonReader.readJsonArrayFromUrl(urlString);

			for (Object o : jsonArray) {
				JSONObject jBadge = (JSONObject) o;

				String address = (String) jBadge.get("address");
				// Is Badge Address in XMLS Badges?
				// yes => do nothing
				// no => add to DB
				BadgeList badgeList = sickDatabase.getBadgeList();
				if (!badgeList.isBadgeInDataBase(address)) {
					sickDatabase.addToBadgeList(new Badge(address, RoleType.ROLE_VISITOR));
					log.info("Badge added! ");
				}

				Boolean connected = (Boolean) jBadge.get("connected");

				// Is connected == true?
				// yes => get role from XML Badge
				// && create ActiveBadge
				// no => ignore
				if (connected) {
					Badge badge = sickDatabase.getBadgeByAddress(address);
					ActiveBadge activeBadge = new ActiveBadge(badge);
					log.info("Badge connected! ");
				}
			}

			log.info("Devices found: " + jsonArray.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// sickDatabase.createActiveBadges(jsonObject);
	}

	public WebSocket getWebsocket() {
		return websocket;
	}
}