package main.htw.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.utils.ConnectionStatusType;

public class LightHandler {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private static final String RED = "\"xy\": [0.734662, 0.265047]";
	private static final String YELLOW = "\"xy\": [0.499226, 0.478163]";
	private static final String GREEN = "\"xy\": [0.126289, 0.815775]";
	// TODO: Blue Colore

	private static CFGPropertyManager propManager = null;

	private static Object lock = new Object();
	private static LightHandler instance = null;

	private static URL url;
	private static ConnectionStatusType connectionStatus;

	private LightHandler() {
		// Use getInstance
	}

	public static LightHandler getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new LightHandler();

					if (propManager == null) {
						propManager = CFGPropertyManager.getInstance();
					}
					url = new URL(propManager.getProperty(PropertiesKeys.LIGHT_BASE_URL));

					setStatusPending();
					initializeConnection();
				}
			}
		}
		return (instance);
	}

	private static void initializeConnection() {
		try {

			// HTW = http://141.56.180.9/api/0F0A018180/lights/1/state
			// S!CK = http://192.168.8.1/api/C02773CB34/lights/1/state

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);

			if (conn.getResponseCode() != 200) {
				setStatusError();
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			setStatusOK();
			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void setLigthGreen(HttpURLConnection conn) throws IOException {

		conn.setRequestMethod("PUT");
		conn.setRequestProperty("Content-Type", "application/json");

		// on = true | off = false
		String input = "{ \"on\": true, " + LightHandler.GREEN + "}";

		OutputStream os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		String output;
		log.info("Output from Server: ");
		while ((output = br.readLine()) != null) {
			log.info(output);
		}
	}

	public static ConnectionStatusType getConnectionStatus() {
		return connectionStatus;
	}

	public static void setStatusOK() {
		connectionStatus = ConnectionStatusType.ALIVE;
	}

	public static void setStatusPending() {
		connectionStatus = ConnectionStatusType.CONNECTING;
	}

	public static void setStatusError() {
		connectionStatus = ConnectionStatusType.DEAD;
	}
}
