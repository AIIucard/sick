package main.htw.handler;

import java.io.FileNotFoundException;
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
import main.htw.datamodell.ActiveBadge;
import main.htw.datamodell.RoleType;
import main.htw.manager.BadgeManager;
import main.htw.parser.JavaToJson;
import main.htw.parser.JsonReader;
import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.xml.Area;
import main.htw.xml.AreaComparator;
import main.htw.xml.AreaList;
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

	private WebSocketFactory factory = new WebSocketFactory();
	private WebSocket websocket;
	private static final String REGISTER_GEO_FENCE_MSG = "{\"topic\":\"REGISTER\",\"payload\":[\"GEOFENCING_EVENT\"]}";
	private static final String REGISTER_POSITION_MSG = "{\"topic\":\"REGISTER\",\"payload\":[\"POSITION\"]}";

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
						// TODO: Log
						e.printStackTrace();
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

	public void addArea(Area area) {
		log.info("Adding new Area to Zigpos...");
		String jsonFormattedString = JavaToJson.getAreaJson(area);
		log.info("Our fine litte json Text IS: " + jsonFormattedString);

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

			log.info("magic happened here i guess");
			// websocket.sendText("");

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void editArea(Area editArea) {
		log.info("Editing Area in Zigpos...");
		log.warn("NOT IMPLEMENTED");
	}

	public List<Area> getAllAreas() {
		Long sickLayer = Long.parseLong(propManager.getProperty(PropertiesKeys.AREA_LAYER));
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

		List<Area> zigposAreaList = new ArrayList<Area>();

		for (Object o : jsonArray) {
			JSONObject jArea = (JSONObject) o;

			Long id = (Long) jArea.get("id");
			Long layer = (Long) jArea.get("layer");
			String name = (String) jArea.get("name");
			// JSONObject shape = (JSONObject) jArea.get("shape");
			// JSONArray coordinates = (JSONArray) shape.get("coordinates");

			if (layer == sickLayer) {
				Area newZigposArea = new Area(Integer.valueOf(id.intValue()), name, Integer.valueOf(layer.intValue()));

				// Check if area already exists in database
				SickDatabase database = SickDatabase.getInstance();
				AreaList areaList = database.getAreaList();
				if (areaList != null) {
					for (Area currentArea : areaList.getAreas()) {
						if (currentArea.getId() == newZigposArea.getId()) {
							newZigposArea.setDistanceToRobot(currentArea.getDistanceToRobot());
						}
					}

					zigposAreaList.add(newZigposArea);

				} else {
					log.error("Area list is null!");
				}
			}
		}

		return zigposAreaList;
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
		JSONArray jsonBadgeArray;
		try {
			// Read JSON from URL
			String urlString = propManager.getProperty(PropertiesKeys.HTTPS_PROTOCOL)
					+ propManager.getProperty(PropertiesKeys.ZIGPOS_BASE_URL) + "/devices";
			jsonBadgeArray = JsonReader.readJsonArrayFromUrl(urlString);

			for (Object badgeObj : jsonBadgeArray) {
				JSONObject jsonBadge = (JSONObject) badgeObj;

				String address = (String) jsonBadge.get("address");
				// Is Badge Address in XMLS Badges?
				// yes => do nothing
				// no => add to DB
				BadgeList badgeList = sickDatabase.getBadgeList();
				if (!BadgeManager.isBadgeInDataBase(address)) {
					badgeList.addBadge(new Badge(address, RoleType.VISITOR));
					log.info("Badge added! ");
				}

				Boolean connected = (Boolean) jsonBadge.get("connected");

				// Is connected == true?
				// yes => get role from XML Badge
				// && create ActiveBadge
				// no => ignore
				if (connected) {
					Badge badge = BadgeManager.getBadgeByAddress(address);
					ActiveBadge activeBadge = new ActiveBadge(badge);
					if (!BadgeManager.isActiveBadgeInDataBase(activeBadge.getAddress())) {
						sickDatabase.getActiveBadgesList().add(activeBadge);
						log.info("Badge connected! ");
					}
				}
			}
			log.info("Devices found: " + jsonBadgeArray.size());
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
	}

	public WebSocket getWebsocket() {
		return websocket;
	}

	public void updateAreas() {
		// TODO Auto-generated method stub

	}

	public ArrayList<ActiveArea> getActiveAreasFromZigpos() {

		// Sort Areas by distance
		List<Area> areas = getAllAreas();
		Collections.sort(areas, new AreaComparator());

		ArrayList<ActiveArea> activeAreas = new ArrayList<ActiveArea>();
		CFGPropertyManager propManager = null;
		propManager = CFGPropertyManager.getInstance();
		int sickPosArea = Integer.parseInt(propManager.getProperty(PropertiesKeys.AREA_LAYER));

		// Check for layer and add Areas to ActiveAreas
		int currentLevel = 0;
		for (Area a : areas) {
			if (a.getLayer() == sickPosArea) {
				ActiveArea activeArea = new ActiveArea(a, currentLevel);
				currentLevel++;
				activeAreas.add(activeArea);
			}
		}
		activeAreas.sort(Comparator.comparing(ActiveArea::getLevel));

		return activeAreas;
	}
}