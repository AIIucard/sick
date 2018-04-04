package main.htw.database;

public class SickDatabase {

	private int currentGeoFenceLevel;

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
