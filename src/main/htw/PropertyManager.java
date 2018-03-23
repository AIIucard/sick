package main.htw;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

public class PropertyManager {
	// TODO: Fix this
	private final static String DEFAULT_CFG_FILE = "cfg" + File.separator + "default.cfg";
	private final static String USER_CFG_FILE = "cfg" + File.separator + "user.cfg";

	private Properties defaultProps = new Properties();
	private Properties userProps = null;

	private static Object lock = new Object();
	private static PropertyManager instance = null;

	private Hashtable propertiesHashTable = null;

	private PropertyManager() {
	}

	public static PropertyManager getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new PropertyManager();
					instance.loadProperties();
				}
			}
		}
		return (instance);

	}

	private void loadProperties() throws IOException {

		// create and load default properties
		FileInputStream inputStream = new FileInputStream(DEFAULT_CFG_FILE);
		defaultProps.load(inputStream);
		inputStream.close();

		// create application properties with default
		defaultProps = new Properties(defaultProps);

		try {
			// user/application properties
			inputStream = new FileInputStream(USER_CFG_FILE);
			userProps.load(inputStream);
			inputStream.close();
		} catch (Throwable th) {
			// TODO: log something
		}
	}

	public void storeProperties() throws IOException {

		FileOutputStream out = new FileOutputStream(DEFAULT_CFG_FILE);
		defaultProps.store(out, "---Default properties---");
		out.close();

		out = new FileOutputStream(USER_CFG_FILE);
		userProps.store(out, "---App/User properties---");
		out.close();
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

		ArrayList<?> list = null;
		Object oldValue = null;

		oldValue = getProperty(key);

		userProps.setProperty(key, val);
		if (propertiesHashTable.containsKey(key)) {
			list = (ArrayList) propertiesHashTable.get(key);
			int len = list.size();
			if (len > 0) {
				PropertyChangeEvent evt = new PropertyChangeEvent(this, key, oldValue, val);
				for (int i = 0; i < len; i++) {
					if (list.get(i) instanceof PropertyChangeListener) {
						((PropertyChangeListener) list.get(i)).propertyChange(evt);
					}
				}
			}
		}
	}

	public boolean addProperty(String key, PropertyChangeListener listener) {
		boolean added = false;
		ArrayList list = null;
		if (propertiesHashTable == null)
			propertiesHashTable = new Hashtable();

		if (!propertiesHashTable.contains(key)) {
			list = new ArrayList();
			added = list.add(listener);
			propertiesHashTable.put(key, list);
		} else {
			list = (ArrayList) propertiesHashTable.get(key);
			added = list.add(listener);
		}
		return (added);
	}

	public void removeListener(PropertyChangeListener listener) {
		if (propertiesHashTable != null && propertiesHashTable.size() > 0)
			propertiesHashTable.remove(listener);
	}
}
