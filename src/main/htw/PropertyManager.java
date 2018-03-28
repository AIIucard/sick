package main.htw;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

public class PropertyManager {
	private final static String DEFAULT_CFG_FILE = "cfg" + File.separator + "default.cfg";
	private final static String USER_CFG_FILE = "cfg" + File.separator + "user.cfg";

	private Properties defaultProps = new Properties();
	private Properties userProps = null;

	private static Object lock = new Object();
	private static PropertyManager instance = null;

	private Hashtable<String, ArrayList<PropertyChangeListener>> listeners = null;

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
		FileInputStream in = new FileInputStream(DEFAULT_CFG_FILE);
		defaultProps.load(in);
		in.close();

		// create application properties with default
		userProps = new Properties(defaultProps);

		try {
			// user/application properties
			in = new FileInputStream(USER_CFG_FILE);
			userProps.load(in);
			in.close();
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
		if (listeners.containsKey(key)) {
			list = (ArrayList<?>) listeners.get(key);
			int len = list.size();
			if (len > 0) {
				PropertyChangeEvent evt = new PropertyChangeEvent(this, key, oldValue, val);
				for (int i = 0; i < len; i++) {
					if (list.get(i) instanceof PropertyChangeListener)
						((PropertyChangeListener) list.get(i)).propertyChange(evt);
				}
			}
		}

	}

	public boolean addListener(String key, PropertyChangeListener listener) {
		boolean added = false;
		ArrayList<PropertyChangeListener> list = null;
		if (listeners == null)
			listeners = new Hashtable<String, ArrayList<PropertyChangeListener>>();

		if (!listeners.contains(key)) {
			list = new ArrayList<PropertyChangeListener>();
			added = list.add(listener);
			listeners.put(key, list);
		} else {
			list = (ArrayList<PropertyChangeListener>) listeners.get(key);
			added = list.add(listener);
		}
		return (added);
	}

	public void removeListener(PropertyChangeListener listener) {
		String keyToDelete = null;
		if (listeners != null && listeners.size() > 0) {
			for (Entry<String, ArrayList<PropertyChangeListener>> entry : listeners.entrySet()) {
				for (PropertyChangeListener currentListener : entry.getValue()) {
					if (Objects.equals(listener, currentListener)) {
						keyToDelete = entry.getKey();
						break;
					}
				}
			}

		}
		if (keyToDelete != null) {
			listeners.remove(keyToDelete);
		}
	}
}
