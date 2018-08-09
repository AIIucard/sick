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
	 * Gets the single instance of LightHandler.
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
						url = new URL(propManager.getProperty(PropertiesKeys.LIGHT_BASE_URL));
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
	 * Initialize connection.
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
	 * Sets the request settings.
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
	 * Send light.
	 *
	 * @param color
	 *            the color
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
	 * Sets the light.
	 *
	 * @param color
	 *            the new light
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
