package main.htw.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLPropertyManager {

	private final static String VIRTUAL_FENCE_PROPERTIES_FILE = "cfg" + File.separator + "default.cfg";

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private Properties prop = new Properties();

	private static Object lock = new Object();
	private static XMLPropertyManager instance = null;

	private XMLPropertyManager() {
		// Use getInstance
	}

	public static XMLPropertyManager getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new XMLPropertyManager();
					instance.loadProperties();
				}
			}
		}

		return (instance);
	}

	public void loadProperties() throws IOException {

		// create and load fence properties
		FileInputStream in = new FileInputStream(VIRTUAL_FENCE_PROPERTIES_FILE);

		try {
			log.info("Load virtual fence properties file...");
			prop.loadFromXML(in);
			in.close();
			log.info("Virtual fence properties loaded.");
		} catch (Throwable th) {
			log.error("Cannot load virtual fence properties!\n" + th.getLocalizedMessage());
		}
	}

	public void storeProperties() throws IOException {
		FileOutputStream out = new FileOutputStream(VIRTUAL_FENCE_PROPERTIES_FILE);
		prop.storeToXML(out, "");
		out.close();
		log.info("Stored virtual fence properties.");
	}

	public String getProperty(String key) {
		return prop.getProperty(key);
	}
}
