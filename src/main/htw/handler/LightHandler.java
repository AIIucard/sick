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

import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.utils.SickColor;

public class LightHandler extends SickHandler {

	private static final String RED = "\"xy\": [0.734662, 0.265047]";
	private static final String YELLOW = "\"xy\": [0.499226, 0.478163]";
	private static final String GREEN = "\"xy\": [0.126289, 0.815775]";
	private static final String BLUE = "\"xy\": [0.157309, 0.0214311]";
	private static final String WHITE = "\"xy\": [0.157309, 0.0214311]"; // TODO change in white

	private static HttpURLConnection connection;

	private static Object lock = new Object();
	private static LightHandler instance = null;

	private static URL url;

	private LightHandler() {
		// Use getInstance
	}

	public static LightHandler getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new LightHandler();
					try {
						propManager = CFGPropertyManager.getInstance();
						// HTW = http://141.56.180.9/api/0F0A018180/lights/1/state
						// S!CK = http://192.168.8.1/api/C02773CB34/lights/1/state
						// url = new URL("http://141.56.180.9/api/0F0A018180/lights/1/state");
						url = new URL(propManager.getProperty(PropertiesKeys.LIGHT_BASE_URL));
					} catch (IOException e) {
						// TODO: Log
						e.printStackTrace();
					}
				}
			}
		}
		return (instance);
	}

	private static void initializeConnection() {
		try {
			log.debug("Opening Connection");
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			setRequestSettings();
			connection.disconnect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void setRequestSettings() {
		try {
			log.debug("Setting Request Settings");
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/json");
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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

	private void setLightGreen() {
		sendLight(LightHandler.GREEN);
	}

	private void setLightRed() {
		sendLight(LightHandler.RED);
	}

	private void setLightYellow() {
		sendLight(LightHandler.YELLOW);
	}

	private void setLightBlue() {
		sendLight(LightHandler.BLUE);
	}

	private void setLightWhite() {
		sendLight(LightHandler.WHITE);
	}

	public void setLight(SickColor color) {
		switch (color) {
		case BLUE:
			setLightBlue();
			break;
		case GREEN:
			setLightGreen();
			break;
		case YELLOW:
			setLightYellow();
			break;
		case RED:
			setLightRed();
			break;
		case WHITE:
			setLightWhite();
			break;
		}
	}
}
