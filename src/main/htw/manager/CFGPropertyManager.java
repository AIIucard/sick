package main.htw.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.properties.CleanProperties;
import main.htw.properties.PropertiesKeys;

/**
 * CFGPropertyManager is a singleton, so the instantiation is restricted to one
 * object. The CFGPropertyManager manages the properties of the application. The
 * original properties are stored in the default.cfg file. All properties
 * changed by the user are stored in user.cfg.
 */
public class CFGPropertyManager {
	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private final static String DEFAULT_CFG_FILE = "cfg" + File.separator + "default.cfg";
	private final static String USER_CFG_FILE = "cfg" + File.separator + "user.cfg";

	private Properties defaultProps = new CleanProperties();
	private Properties userProps = null;

	private static Object lock = new Object();
	private static CFGPropertyManager instance = null;

	/**
	 * Use <code>getInstance</code> method!
	 * 
	 * @deprecated
	 */
	private CFGPropertyManager() {
		// Use getInstance
	}

	/**
	 * Realizes the singleton pattern with synchronized(lock), ensures that just one
	 * class can instantiate this class in one specific moment. If there is already
	 * an instance of this class, the method returns a reference. With the
	 * instantiation of the class, all property files are loaded.
	 *
	 * @return the new or referenced instance of this class.
	 */
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

	/**
	 * Load the properties from the default.cfg and user.cfg file. The file
	 * destination can be changed inside this class.
	 */
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

	/**
	 * Save the properties to the default.cfg and user.cfg file. The file
	 * destination can be changed inside this class.
	 * 
	 * @throws IOException
	 *             if the files can't be open by the application.
	 */
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

	/**
	 * Get a specific property value by its key. All property keys can be found
	 * inside the {@link PropertiesKeys}.
	 * 
	 * @param key
	 *            the key for the property.
	 * @return the value of the property.
	 */
	public String getPropertyValue(String key) {
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
		Object oldValue = getPropertyValue(key);
		if (oldValue != val) {
			userProps.setProperty(key, val);
		}
	}
}
