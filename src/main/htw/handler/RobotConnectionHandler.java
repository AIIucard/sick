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

public class RobotConnectionHandler extends SickHandler {

	private static Object lock = new Object();
	private static RobotConnectionHandler instance = null;

	private static URL url;
	private static HttpURLConnection connection;

	private RobotConnectionHandler() {
		// Use getInstance
	}

	public static RobotConnectionHandler getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new RobotConnectionHandler();
					try {
						propManager = CFGPropertyManager.getInstance();
						url = new URL(propManager.getProperty(PropertiesKeys.SICK_ROBOT_WEBSERVICE_URL));
					} catch (MalformedURLException e) {
						log.error("Can not create new URL for RobotConnectionHandler! Got the following Exception: "
								+ e.getLocalizedMessage());
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
			log.error("Can not initialize Connection in LightHandler! Got the following Exception: "
					+ e.getLocalizedMessage());
		} catch (IOException e) {
			log.error("Can not initialize Connection in LightHandler! Got the following Exception: "
					+ e.getLocalizedMessage());
		}
	}

	private static void setRequestSettings() {
		try {
			log.debug("Setting Request Settings");
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/json");
		} catch (ProtocolException e) {
			log.error("Can not load settings in RobotHandler! Got the following Exception: " + e.getLocalizedMessage());
		}
	}

	private static void sendSecurityLevel(Integer securitylevel) {
		initializeConnection();
		setRequestSettings();
		try {
			connection.connect();

			// on = true | off = false
			String input = "{ SecurityLevel:" + securitylevel + "}";

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
}
