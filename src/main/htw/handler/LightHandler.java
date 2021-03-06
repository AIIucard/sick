package main.htw.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import main.htw.manager.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.utils.SickColor;

/**
 * LigthHandler is a singleton, so the instantiation is restricted to one
 * object. Because there is just one object needed to coordinate the following
 * actions.
 * 
 * <ul>
 * <li>Initializes the Connection to the light strip
 * <li>Send the calculated color to the light strip via REST
 * </ul>
 *
 */
public class LightHandler extends SickHandler {

	private static final String RED = "\"xy\": [0.734662, 0.265047]";
	private static final String YELLOW = "\"xy\": [0.499226, 0.478163]";
	private static final String GREEN = "\"xy\": [0.126289, 0.815775]";
	private static final String BLUE = "\"xy\": [0.157309, 0.0214311]";
	private static final String WHITE = "\"xy\": [0.157294, 0.0214311]";

	private static HttpURLConnection connection;

	private static Object lock = new Object();
	private static LightHandler instance = null;

	private static URL url;

	/**
	 * Use <code>getInstance</code> method!
	 * 
	 * @deprecated
	 */
	private LightHandler() {
		// Use getInstance
	}

	/**
	 * Realizes the singleton pattern with synchronized(lock), ensures that just one
	 * class can instantiate this class in one specific moment. If there is already
	 * an instance of this class, the method returns a reference. The class get
	 * instantiated with a URL which is defined and loaded from config file.
	 *
	 * @return single instance of LightHandler
	 */
	public static LightHandler getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new LightHandler();
					try {
						propManager = CFGPropertyManager.getInstance();
						url = new URL(propManager.getPropertyValue(PropertiesKeys.LIGHT_BASE_URL));
					} catch (MalformedURLException e) {
						log.error("Can not create new URL for LightHandler! Got the following Exception: "
								+ e.getLocalizedMessage());
					}
				}
			}
		}
		return (instance);
	}

	/**
	 * Initializes the connection to the loaded URL of the LightHandler instance.
	 * Set the DoOutput flag to true to use the URL connection for output. If the
	 * connection attempt is successful, sets database connection flag OK.
	 */
	private static void initializeConnection() {
		try {
			log.debug("Opening Connection");
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			setRequestSettings();
			connection.disconnect();
		} catch (MalformedURLException e) {
			log.error("Can not initialize Connection in LightHandler! Got the following Exception: "
					+ e.getLocalizedMessage());
		} catch (IOException e) {
			log.error("Can not initialize Connection in LightHandler! Got the following Exception: "
					+ e.getLocalizedMessage());
		}
	}

	/**
	 * Sets the REST request settings. "PUT" is set as the REST method for changing
	 * the LED color. The media type (content type) is defined with
	 * "application/json".
	 */
	private static void setRequestSettings() {
		try {
			log.debug("Setting Request Settings");
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/json");
		} catch (ProtocolException e) {
			log.error("Can not load settings in LightHandler! Got the following Exception: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Send the light color to the light module via the established connection.
	 * The method is called in the method setLight. Initializes the connection to
	 * the light strip. Uses request settings to finally send the calculated LED
	 * color to the light strip via REST-API. For control purposes, the server's
	 * response is output using an output stream.
	 *
	 * @param color
	 *            color for light strip from method setLight
	 */
	private static void sendLight(String color) {
		initializeConnection();
		setRequestSettings();
		try {
			connection.connect();

			// on = true | off = false
			String input = "{ \"on\": true, " + color + "}";

			log.debug("Getting Output Stream");
			OutputStream os = connection.getOutputStream();
			log.debug("Writing input");
			os.write(input.getBytes());
			log.debug("Flushing Output Stream");
			os.flush();
			log.debug("Closing Output Stream");
			os.close();

			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader((is)));

			String output;
			log.debug("Output from Server: ");
			while ((output = br.readLine()) != null) {
				log.info(output);
			}

			if (connection.getResponseCode() != 200) {
				throw new RuntimeException();
			}

			connection.disconnect();
		} catch (IOException e) {
			log.error("Request failed! " + e.getLocalizedMessage());
		}
	}

	/**
	 * Sets the Sick light color.
	 *
	 * @param color
	 *            the new light color.
	 */
	public void setLight(SickColor color) {
		switch (color) {
		case BLUE:
			sendLight(LightHandler.BLUE);
			break;
		case GREEN:
			sendLight(LightHandler.GREEN);
			break;
		case YELLOW:
			sendLight(LightHandler.YELLOW);
			break;
		case RED:
			sendLight(LightHandler.RED);
			break;
		case WHITE:
			sendLight(LightHandler.WHITE);
			break;
		}
	}
}
