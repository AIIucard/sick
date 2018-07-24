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
	private boolean isRobotReconnecting = false;
	private ConnectionStatusType rtlsConnectionStatus = ConnectionStatusType.NEW;
	private boolean isRTLSReconnecting = false;
	private ConnectionStatusType lightConnectionStatus = ConnectionStatusType.NEW;
	private boolean isLightReconnecting = false;

	private ArrayList<ActiveArea> activeAreasList = new ArrayList<ActiveArea>();
	private ArrayList<ActiveBadge> activeBadgesList = new ArrayList<ActiveBadge>();
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

	public void setActiveBadgesList(ArrayList<ActiveBadge> activeBadgesList) {
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

	public boolean isRobotReconnecting() {
		return isRobotReconnecting;
	}

	public void setRobotReconnecting(boolean isRobotReconnecting) {
		this.isRobotReconnecting = isRobotReconnecting;
	}

	public boolean isRTLSReconnecting() {
		return isRTLSReconnecting;
	}

	public void setRTLSReconnecting(boolean isRTLSReconnecting) {
		this.isRTLSReconnecting = isRTLSReconnecting;
	}

	public boolean isLightReconnecting() {
		return isLightReconnecting;
	}

	public void setLightReconnecting(boolean isLightReconnecting) {
		this.isLightReconnecting = isLightReconnecting;
	}
}
