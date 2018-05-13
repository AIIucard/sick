package main.htw.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;

public class LightHandler extends SickHandler {

	private static final String RED = "\"xy\": [0.734662, 0.265047]";
	private static final String YELLOW = "\"xy\": [0.499226, 0.478163]";
	private static final String GREEN = "\"xy\": [0.126289, 0.815775]";
	// TODO: Blue Color

	private static HttpURLConnection Connection;

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

					if (propManager == null) {
						try {
							propManager = CFGPropertyManager.getInstance();
							url = new URL(propManager.getProperty(PropertiesKeys.LIGHT_BASE_URL));
						} catch (IOException e) {
							// TODO: Log
							e.printStackTrace();
						}
					}
				}
			}
		}
		return (instance);
	}

	public void initializeConnection() throws Exception {
		// HTW = http://141.56.180.9/api/0F0A018180/lights/1/state
		// S!CK = http://192.168.8.1/api/C02773CB34/lights/1/state

		URL url = new URL("http://141.56.180.9/api/0F0A018180/lights/1/state");
		log.info("Opening Connection");
		Connection = (HttpURLConnection) url.openConnection();// (HttpURLConnection)
																// url.openConnection();
		Connection.setDoOutput(true);
		setRequestSettings();
		Connection.disconnect();
	}

	private static void setRequestSettings() throws IOException {

		log.info("Setting Request Settings");
		Connection.setRequestMethod("PUT");
		Connection.setRequestProperty("Content-Type", "application/json");
	}

	private static void setLight(String color) throws IOException {
		Connection.connect();

		// on = true | off = false
		String input = "{ \"on\": true, " + color + "}";

		log.info("Getting Output Stream");
		OutputStream os = Connection.getOutputStream();
		log.info("Writing input");
		os.write(input.getBytes());
		log.info("Flushing Output Stream");
		os.flush();
		log.info("Closing Output Stream");
		os.close();

		InputStream is = Connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader((is)));

		String output;
		log.info("Output from Server: ");
		while ((output = br.readLine()) != null) {
			log.info(output);
		}

		if (Connection.getResponseCode() != 200) {
			throw new RuntimeException("Request failed! HTTP error code : " + Connection.getResponseCode());
		}

		Connection.disconnect();
	}

	public void setLigthGreen() {

		if (instance == null) {
			log.error("Light Handler not initialized!");
			return;
		}

		try {
			setLight(LightHandler.GREEN);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("Could not set light to green!");
			e.printStackTrace();
		}
	}

	public void setLigthRed() {

		if (instance == null) {
			log.error("Light Handler not initialized!");
			return;
		}

		try {
			setLight(LightHandler.RED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("Could not set light to red!");
			e.printStackTrace();
		}
	}

	public void setLigthYellow() {

		if (instance == null) {
			log.error("Light Handler not initialized!");
			return;
		}

		try {
			setLight(LightHandler.YELLOW);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("Could not set light to yellow!");
			e.printStackTrace();
		}
	}

}
