package main.htw.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import main.htw.datamodell.ActiveArea;
import main.htw.datamodell.ActiveBadge;
import main.htw.utils.ConnectionStatusType;
import main.htw.xml.AreaList;
import main.htw.xml.BadgeList;

public class SickDatabase extends Observable {

	private int currentGeoFenceLevel = -1;
	private AreaList areaList = null;
	private BadgeList badgeList = null;
	private boolean godModeActive = false;

	private double robotPositionX = 0;
	private double robotPositionY = 0;

	private ConnectionStatusType robotConnectionStatus = ConnectionStatusType.NEW;
	private ConnectionStatusType rtlsConnectionStatus = ConnectionStatusType.NEW;
	private ConnectionStatusType lightConnectionStatus = ConnectionStatusType.NEW;

	private ArrayList<ActiveArea> activeAreasList = new ArrayList<ActiveArea>();
	private List<ActiveBadge> activeBadgesList = new ArrayList<ActiveBadge>();
	private ActiveArea nearestActiveArea = null;

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

	// // TODO Remove try catch + logger
	// public void addToBadgeList(Badge badge) {
	// if (badgeList == null) {
	// badgeList = new BadgeList();
	// }
	//
	// SickDatabase.badgeList.addBadge(badge);
	//
	// try {
	// xmlMarshaller = XMLMarshler.getInstance();
	// if (xmlMarshaller != null && badgeList != null) {
	// xmlMarshaller.marshalBadgeList(badgeList);
	// } else {
	// log.error("Cannot store badges!");
	// }
	// } catch (JAXBException e) {
	// log.error("Cannot store badges! JAXBException thrown: " + e);
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	// public void removeFromBadgeList(Badge badge) {
	// SickDatabase.badgeList.removeBadge(badge);
	// }

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
		this.areaList = areaList;
	}

	public BadgeList getBadgeList() {
		return badgeList;
	}

	public void setBadgeList(BadgeList badgeList) {
		this.badgeList = badgeList;
	}

	public boolean isGodModeActive() {
		return godModeActive;
	}

	public void setGodMode(boolean godMode) {
		this.godModeActive = godMode;
	}

	public double getRobotPositionX() {
		return robotPositionX;
	}

	public void setRobotPositionX(double robotPositionX) {
		this.robotPositionX = robotPositionX;
	}

	public double getRobotPositionY() {
		return robotPositionY;
	}

	public void setRobotPositionY(double robotPositionY) {
		this.robotPositionY = robotPositionY;
	}

	public ArrayList<ActiveArea> getActiveAreasList() {
		return activeAreasList;
	}

	public void setActiveAreasList(ArrayList<ActiveArea> activeAreasList) {
		this.activeAreasList = activeAreasList;
	}

	public List<ActiveBadge> getActiveBadgesList() {
		return activeBadgesList;
	}

	public void setActiveBadgesList(List<ActiveBadge> activeBadgesList) {
		this.activeBadgesList = activeBadgesList;
	}

	public ActiveArea getNearestActiveArea() {
		return nearestActiveArea;
	}

	public void setNearestActiveArea(ActiveArea nearestActiveArea) {
		this.nearestActiveArea = nearestActiveArea;
	}

	public ConnectionStatusType getRobotConnectionStatus() {
		return robotConnectionStatus;
	}

	public void setRobotConnectionStatus(ConnectionStatusType robotConnectionStatus) {
		setChanged();
		this.robotConnectionStatus = robotConnectionStatus;
		notifyObservers();
	}

	public ConnectionStatusType getRTLSConnectionStatus() {
		return rtlsConnectionStatus;
	}

	public void setRTLSConnectionStatus(ConnectionStatusType rtlsConnectionStatus) {
		setChanged();
		this.rtlsConnectionStatus = rtlsConnectionStatus;
		notifyObservers();
	}

	public ConnectionStatusType getLightConnectionStatus() {
		return lightConnectionStatus;
	}

	public void setLightConnectionStatus(ConnectionStatusType lightConnectionStatus) {
		setChanged();
		this.lightConnectionStatus = lightConnectionStatus;
		notifyObservers();
	}
}
