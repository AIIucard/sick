package main.htw.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SickDatabase {

	private int currentGeoFenceLevel;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public SickDatabase() {
		setCurrentGeoFenceLevel(-1);
	}

	public int getCurrentGeoFenceLevel() {
		return currentGeoFenceLevel;
	}

	public void setCurrentGeoFenceLevel(int currentGeoFenceLevel) {
		this.currentGeoFenceLevel = currentGeoFenceLevel;
	}
}
