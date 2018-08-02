package main.htw.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import main.htw.datamodell.ActiveArea;
import main.htw.datamodell.RoleType;
import main.htw.manager.AreaManager;
import main.htw.manager.BadgeManager;
import main.htw.manager.CFGPropertyManager;
import main.htw.parser.JavaToJsonParser;
import main.htw.parser.JsonReader;
import main.htw.properties.PropertiesKeys;
import main.htw.utils.AreaComparator;
import main.htw.utils.SickUtils;
import main.htw.xml.Area;
import main.htw.xml.Badge;

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

	private static Object lock = new Object();
	private static RTLSHandler instance = null;

	private static URI uri;

	private RTLSHandler() {
		// Use getInstance
	}

	public static RTLSHandler getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new RTLSHandler();
					try {
						propManager = CFGPropertyManager.getInstance();
						String websocketString = propManager.getProperty(PropertiesKeys.WEBSOCKET_PROTOCOL)
								+ propManager.getProperty(PropertiesKeys.ZIGPOS_BASE_URL) + "/socket";
						uri = new URI(websocketString);

					} catch (URISyntaxException e) {
						log.error("Can not create new URI for RTLSHandler! Got the following Exception: "
								+ e.getLocalizedMessage());
					}
				}
			}
		}
		return (instance);
	}

	public void initializeConnection() throws Exception {
		log.info("Connecting to RTLS at " + uri + "...");
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
		log.info("Registering to topic GEOFENCEING_EVENT...");
		if (sickMessageHandler == null) {
			log.warn("SickMessagehandler is not initiliazed!");
		}
		websocket.addListener(sickMessageHandler);
		try {
			websocket.connect();
			websocket.sendText(RTLSHandler.REGISTER_GEO_FENCE_MSG);
		} catch (WebSocketException e) {
			log.error("Can not initialize Websocket in RTLSHandler! Got the following Exception: "
					+ e.getLocalizedMessage());
		}
	}

	public void addAreaToZigpos(Area area) {
		log.info("Adding new Area to Zigpos...");
		String jsonFormattedString = JavaToJsonParser.getAreaJson(area);
		try {
			String websocketString = propManager.getProperty(PropertiesKeys.HTTPS_PROTOCOL)
					+ propManager.getProperty(PropertiesKeys.ZIGPOS_BASE_URL) + "/geofencing/areas";

			URL url = new URL(websocketString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
			String input = jsonFormattedString;

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTPS error code : " + conn.getResponseCode());
			}

			conn.disconnect();
		} catch (MalformedURLException e) {
			log.error("Can not add area to Zigpos! MalformedURLException thrown: " + e.getLocalizedMessage());
		} catch (IOException e) {
			log.error("Can not add area to Zigpos! IOException thrown: " + e.getLocalizedMessage());
		}
	}

	public void updateAreaInZigpos(Area area) {
		log.info("Updating Area in Zigpos...");
		String jsonFormattedString = JavaToJsonParser.getAreaJson(area);
		try {
			String websocketString = propManager.getProperty(PropertiesKeys.HTTPS_PROTOCOL)
					+ propManager.getProperty(PropertiesKeys.ZIGPOS_BASE_URL) + "/geofencing/areas/" + area.getId();

			URL url = new URL(websocketString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
			String input = jsonFormattedString;

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTPS error code : " + conn.getResponseCode());
			}

			conn.disconnect();
		} catch (MalformedURLException e) {
			log.error("Can not update area in Zigpos! MalformedURLException thrown: " + e.getLocalizedMessage());
		} catch (IOException e) {
			log.error("Can not update area in Zigpos! IOException thrown: " + e.getLocalizedMessage());
		}
	}

	public void editArea(Area editArea) {
		updateAreaInZigpos(editArea);
	}

	private List<Area> getAllAreasForLayerFromZigpos(Long sickLayer) {
		String urlString = propManager.getProperty(PropertiesKeys.HTTPS_PROTOCOL)
				+ propManager.getProperty(PropertiesKeys.ZIGPOS_BASE_URL) + "/geofencing/areas";
		JSONArray jsonArray;

		try {
			log.info("Getting All Areas from Zigpos...");
			jsonArray = JsonReader.readJsonArrayFromUrl(urlString);
		} catch (Exception e) {
			log.error("Can not read Areas from Json! Got the following Exception: " + e.getLocalizedMessage());
			return null;
		}

		List<Area> zigposAreaList = new ArrayList<Area>();

		for (Object obj : jsonArray) {
			JSONObject jsonAreaObj = (JSONObject) obj;

			Long id = (Long) jsonAreaObj.get("id");
			Long layer = (Long) jsonAreaObj.get("layer");
			String name = (String) jsonAreaObj.get("name");

			if (layer == sickLayer) {
				if (AreaManager.isAreaInDataBase(id)) {
					Area newArea = new Area(Integer.valueOf(id.intValue()), name, Integer.valueOf(layer.intValue()));
					newArea.setDistanceToRobot(AreaManager.getAreaByID(id).getDistanceToRobot());
					zigposAreaList.add(newArea);
				}
			}
		}
		return zigposAreaList;
	}

	public void getActiveBadges() {
		JSONArray jsonBadgeArray;
		try {
			String urlString = propManager.getProperty(PropertiesKeys.HTTPS_PROTOCOL)
					+ propManager.getProperty(PropertiesKeys.ZIGPOS_BASE_URL) + "/devices";
			jsonBadgeArray = JsonReader.readJsonArrayFromUrl(urlString);

			for (Object badgeObj : jsonBadgeArray) {
				JSONObject jsonBadge = (JSONObject) badgeObj;
				String address = (String) jsonBadge.get("address");
				String name = (String) jsonBadge.get("customName");

				if (!BadgeManager.isBadgeInDataBase(address)) {
					BadgeManager.addBadge(new Badge(address, name, RoleType.VISITOR));
				} else {
					BadgeManager.updateBadgeName(address, name);
				}

				Boolean isBadgeConnected = (Boolean) jsonBadge.get("connected");
				if (isBadgeConnected) {
					Badge badge = BadgeManager.getBadgeByAddress(address);
					BadgeManager.addBadgeToActiveBadges(badge);
				}
			}
			log.info("Devices found: " + jsonBadgeArray.size());
		} catch (IOException e) {
			log.error("Can not load ActiveBadges in RTLSHandler! Got the following IOException: "
					+ e.getLocalizedMessage());
		} catch (JSONException e) {
			log.error("Can not load ActiveBadges in RTLSHandler! Got the following JSONException: "
					+ e.getLocalizedMessage());
		}
	}

	public WebSocket getWebsocket() {
		return websocket;
	}

	public void updateAreas() {
		Long sickLayer = Long.parseLong(propManager.getProperty(PropertiesKeys.AREA_LAYER));
		List<Area> areasInLayer = getAllAreasForLayerFromZigpos(sickLayer);
		List<Area> areasToCheck = new ArrayList<Area>();
		List<Area> areaList = SickDatabase.getInstance().getAreaList().getAreas();
		for (Area area : areaList) {
			areasToCheck.add(area);
		}
		for (Area areaToCheck : areasToCheck) {
			for (Area areaInLayer : areasInLayer) {

				// Check if area exists in layer
				if (areaToCheck.getName().equals(areaInLayer.getName())) {

					// Check if ID is different
					if (areaToCheck.getId() != areaInLayer.getId()) {
						areaToCheck.setId(areaInLayer.getId());
					}

					// Check if coordinates are different
					if (areaToCheck.getId() == areaInLayer.getId()
							&& SickUtils.hasDifferentCoordinates(areaToCheck, areaInLayer)) {
						updateAreaInZigpos(areaToCheck);
					}
				} else {
					addAreaToZigpos(areaToCheck);
				}
			}
		}

		// Update IDs from Areas
		SickDatabase.getInstance().getAreaList().setAreas(areasToCheck);
	}

	public ArrayList<ActiveArea> getActiveAreas() {

		Long sickLayer = Long.parseLong(propManager.getProperty(PropertiesKeys.AREA_LAYER));
		List<Area> areas = getAllAreasForLayerFromZigpos(sickLayer);

		// Sort Areas by distance ==> 0 to 3
		Collections.sort(areas, new AreaComparator());

		ArrayList<ActiveArea> activeAreas = new ArrayList<ActiveArea>();

		// Check for layer and add Areas to ActiveAreas
		int currentLevel = 0;
		for (Area a : areas) {
			if (a.getLayer() == sickLayer.intValue()) {
				ActiveArea activeArea = new ActiveArea(a, currentLevel);
				currentLevel++;
				activeAreas.add(activeArea);
			}
		}
		activeAreas.sort(Comparator.comparing(ActiveArea::getLevel));

		return activeAreas;
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
			log.error("Can not reconnect to Websocket in RTLSHandler! Got the following NoSuchAlgorithmException: "
					+ e.getLocalizedMessage());
		} catch (IOException e) {
			log.error("Can not reconnect to Websocket in RTLSHandler! Got the following IOException: "
					+ e.getLocalizedMessage());
		} catch (WebSocketException e) {
			log.error("Can not reconnect to Websocket in RTLSHandler! Got the following WebSocketException: "
					+ e.getLocalizedMessage());
		}
	}
}