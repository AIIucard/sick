package main.htw.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CFGPropertyManager {
	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private final static String DEFAULT_CFG_FILE = "cfg" + File.separator + "default.cfg";
	private final static String USER_CFG_FILE = "cfg" + File.separator + "user.cfg";

	private Properties defaultProps = new CleanProperties();
	private Properties userProps = null;

	private static Object lock = new Object();
	private static CFGPropertyManager instance = null;

	private CFGPropertyManager() {
		// Use getInstance
	}

	public static CFGPropertyManager getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new CFGPropertyManager();
					instance.loadProperties();
				}
			}
		}

		return (instance);
	}

	private void loadProperties() {

		log.info("Load default properties file...");
		FileInputStream in;
		try {
			in = new FileInputStream(DEFAULT_CFG_FILE);
			defaultProps.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			log.error("Can not load Properties File! Got the following Exception: " + e.getLocalizedMessage());
		} catch (IOException e) {
			log.error("Can not load Properties File! Got the following Exception: " + e.getLocalizedMessage());
		}
		log.info("Default properties loaded.");

		// create application properties with default
		userProps = new CleanProperties(defaultProps);

		try {
			// user/application properties
			log.info("Load user properties file...");
			in = new FileInputStream(USER_CFG_FILE);
			userProps.load(in);
			in.close();
			log.info("User properties loaded.");
		} catch (Throwable th) {
			log.error("Cannot load user properties!\n" + th.getLocalizedMessage());
		}
	}

	public void storeProperties() throws IOException {
		FileOutputStream out = new FileOutputStream(DEFAULT_CFG_FILE);
		defaultProps.store(out, "");
		out.close();
		log.info("Stored default properties.");

		out = new FileOutputStream(USER_CFG_FILE);
		userProps.store(out, "");
		out.close();
		log.info("Stored user properties.");
	}

	public String getProperty(String key) {
		String val = null;
		if (key != null) {
			if (userProps != null)
				val = (String) userProps.getProperty(key);
			if (val == null) {
				val = defaultProps.getProperty(key);
			}
		}

		return (val);
	}

	/**
	 * Sets Application/User String properties; default property values cannot be
	 * set.
	 */
	public void setProperty(String key, String val) {
		Object oldValue = getProperty(key);
		if (oldValue != val) {
			userProps.setProperty(key, val);
		}
	}
}
