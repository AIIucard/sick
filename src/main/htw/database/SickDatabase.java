package main.htw.database;

import main.htw.xml.AreaList;

public class SickDatabase {

	private int currentGeoFenceLevel = -1;
	private static AreaList areaList = null;

	private static double robotPositionX = 0;
	private static double robotPositionY = 0;

	private static Object lock = new Object();
	private static SickDatabase instance = null;

	private SickDatabase() {
		// Use getInstance
	}

	public static SickDatabase getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new SickDatabase();
				}
			}
		}

		return (instance);
	}

	public int getCurrentGeoFenceLevel() {
		return currentGeoFenceLevel;
	}

	public void setCurrentGeoFenceLevel(int currentGeoFenceLevel) {
		this.currentGeoFenceLevel = currentGeoFenceLevel;
	}

	public AreaList getAreaList() {
		return areaList;
	}

	public void setAreaList(AreaList areaList) {
		SickDatabase.areaList = areaList;
	}

	public double getRobotPositionX() {
		return robotPositionX;
	}

	public void setRobotPositionX(double robotPositionX) {
		SickDatabase.robotPositionX = robotPositionX;
	}

	public double getRobotPositionY() {
		return robotPositionY;
	}

	public void setRobotPositionY(double robotPositionY) {
		SickDatabase.robotPositionY = robotPositionY;
	}
}
