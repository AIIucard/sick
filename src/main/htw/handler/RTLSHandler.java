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
 * RobotHandler is a singleton, so the instantiation is restricted to one
 * object. The RobotHandler coordinates the following actions:
 * <ul>
 * <li>Initializes the Connection to the ZigPos RTLS System
 * <li>Registers to all geofencing events from the ZigPos RTLS System
 * <li>Gets, adds and updates areas on the ZigPos RTLS System
 * <li>Retrieves all badges and areas and filters for the relevant badges and
 * areas called "ActiveBadges" and "ActiveAreas"
 * </ul>
 */
@ClientEndpoint
public class RTLSHandler extends SickHandler {

	private SickMessageHandler sickMessageHandler = null;

	private WebSocket websocket;
	private static final String REGISTER_GEO_FENCE_MSG = "{\"topic\":\"REGISTER\",\"payload\":[\"GEOFENCING_EVENT\"]}";

	private static Object lock = new Object();
	private static RTLSHandler instance = null;

	private static URI uri;

	/**
	 * Use <code>getInstance</code> method!
	 * 
	 * @deprecated
	 */
	private RTLSHandler() {
		// Use getInstance
	}

	/**
	 * Realizes the singleton pattern with synchronized (lock), ensures that just
	 * one class can instantiate this class in one specific moment. If there is
	 * already an instance of this class, the method returns a reference. The class
	 * get instantiated with the ZigPos websocket URI which is defined in and loaded
	 * from config file.
	 * <p>
	 * If the method cannot create the URI from the websocket string an exception
	 * will be thrown inside the method and the URI will be Null.
	 *
	 * @return the new or referenced instance of this class.
	 */
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

	/**
	 * Initializes the connection to the loaded URI of the RTLS instance. The method
	 * ignores certificates through setting the verification of the host name to
	 * false. Initializes the <link>SickMessageHandler</link> if not already done.
	 * <p>
	 * If the connection attempt is successful, sets database connection flag OK.
	 * 
	 * @throws Exception
	 *             Exception is generalized and depends on the connection type.
	 *             Handle <code>LocalizedMessage()</code> and try to reconnect!
	 */
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

	/**
	 * Registers the Application to receive all ZigPos event messages for all
	 * Geofencing Events. On calling it will also try to open a websocket with the
	 * <link>SickMessageHandler</link> as Listener.
	 * <p>
	 * If something goes wrong an exception will be logged.
	 */
	public void registerGeoFence() {
		log.info("Registering to topic GEOFENCING_EVENT...");
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

	/**
	 * Adds the given Area to the ZigPos RTLS System. On calling the method creates
	 * an JSON formatted string from the area object and tries to connect to the
	 * ZigPos Geofencing Areas URL. If the connection is successful it will send the
	 * JSON formatted string to the URL and thus add the specified area to the
	 * ZigPos RTLS System for further usage.
	 * <p>
	 * If something goes wrong an exception will be logged.
	 * <p>
	 * If the connection gets the Response 200 an error will be thrown inside the
	 * method.
	 * 
	 * @param area
	 *            Area object which will be added to the ZigPos RTLS System.
	 */
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

	/**
	 * Updates the given Area on the ZigPos RTLS System. On calling the method
	 * creates an JSON formatted string from the area object and tries to connect to
	 * the ZigPos Geofencing Areas URL. If the connection is successful it will send
	 * the JSON formatted string to the URL and thus update the specified and
	 * existing area on the ZigPos RTLS System for further usage.
	 * <p>
	 * If something goes wrong an exception will be logged.
	 * <p>
	 * If the connection gets the Response 200 an error will be thrown inside the
	 * method.
	 * 
	 * @param area
	 *            Area object which will be updated on the ZigPos RTLS System.
	 */
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

	/**
	 * Retrieves a list of area objects belonging to the given layer in the ZigPos
	 * RTLS System. Each Geofence/Area has to be attached to a certain layer in the
	 * ZigPos RTLS System.
	 * 
	 * @param layer
	 *            The layer for which all areas will be retrieved.
	 * @return A list containing Area objects which match the given layer. <br>
	 *         Returns Null of the ZigPos Areas URL doesn't contain parsable JSON or
	 *         if no area matches the given layer.
	 */
	private List<Area> getAllAreasForLayerFromZigpos(Long layer) {
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
			Long areaLayer = (Long) jsonAreaObj.get("layer");
			String name = (String) jsonAreaObj.get("name");

			if (areaLayer == layer) {
				if (AreaManager.isAreaInDataBase(id)) {
					Area newArea = new Area(Integer.valueOf(id.intValue()), name,
							Integer.valueOf(areaLayer.intValue()));
					newArea.setDistanceToRobot(AreaManager.getAreaByID(id).getDistanceToRobot());
					zigposAreaList.add(newArea);
				}
			}
		}
		return zigposAreaList;
	}

	/**
	 * Retrieves all Badges known to the ZigPos RTLS System. For every badge it is
	 * checked whether or not it is already known to the application. If it is a new
	 * badge it will be assigned the <strong> default RoleType "Visitor"</strong>.
	 * If the badge is already known to the application it will update its
	 * attributes.
	 * <p>
	 * For every badge it is checked whether or not the badge is connected to the
	 * RTLS System. Only connected badges will be useful for the application. If the
	 * badge has its connected attribute set it will be added to the
	 * <strong>ActiveBadges</strong>.
	 * <p>
	 * If something goes wrong an exception will be logged.
	 */
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

	/**
	 * @return websocket
	 */
	public WebSocket getWebsocket() {
		return websocket;
	}

	/**
	 * Updates all areas on the ZigPos RTLS System belonging to the so called "Sick
	 * Layer" which houses all areas for a proper execution of this Application. The
	 * Sick Layer can be customized in the configuration file. If the coordinates of
	 * an area have changed it will be updated in the ZigPos RTLS System. If the
	 * area doesn't exist in the ZigPos RTLS System it will be added to it.
	 */
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

	/**
	 * Gets all areas from the ZigPos RTLS System which are on the so called "Sick
	 * Layer" which houses all areas for a proper execution of this Application. The
	 * Sick Layer can be customized in the configuration file. The areas will be
	 * sorted and added to the AreaStack.
	 * 
	 * @return A list of all active areas.
	 */
	public ArrayList<ActiveArea> getActiveAreas() {

		Long sickLayer = Long.parseLong(propManager.getProperty(PropertiesKeys.AREA_LAYER));
		List<Area> areas = getAllAreasForLayerFromZigpos(sickLayer);

		// Sort Areas by distance ==> 0 to 3
		Collections.sort(areas, new AreaComparator());

		ArrayList<ActiveArea> activeAreas = new ArrayList<ActiveArea>();

		// Check for layer and add Areas to ActiveAreas
		// Represents the structure of the Area Stack
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

	/**
	 * If the connection to the ZigPos RLTS System is lost this method will try to
	 * establish a new connection. *
	 * <p>
	 * If something goes wrong an exception will be logged.
	 */
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