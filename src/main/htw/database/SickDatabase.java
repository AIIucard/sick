package main.htw.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import main.htw.datamodell.ActiveArea;
import main.htw.datamodell.ActiveBadge;
import main.htw.utils.ConnectionStatusType;
import main.htw.xml.AreaList;
import main.htw.xml.BadgeList;

/**
 * SickDatabase is a singleton, so the instantiation is restricted to one
 * object. The SickDatabase provides convenience methods for managing the
 * application data at runtime. It contains the following data:
 * <ul>
 * <li>Current Geofence level
 * <li>a list of the configured areas
 * <li>a list of the configured badges
 * <li>a flag if the visitor mode is active or not
 * <li>the exact position of the robot with the corresponding X and Y
 * coordinates
 * <li>the current connection status of the robot connection
 * <li>the current connection status of the RTLS connection
 * <li>the current connection status of the light module connection
 * <li>a flag if the robot, the RTLS or the light module are currently
 * reconnecting
 * <li>a list of the active areas
 * <li>a list of the active badges
 * <li>the nearest active area for the badge placement
 * </ul>
 */
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

	/**
	 * Use <code>getInstance</code> method!
	 * 
	 * @deprecated
	 */
	private SickDatabase() {
		// Use getInstance
	}

	/**
	 * Realizes the singleton pattern with synchronized(lock), ensures that just one
	 * class can instantiate this class in one specific moment. If there is already
	 * an instance of this class, the method returns a reference.
	 *
	 * @return The new or referenced instance of this class.
	 */
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

	/**
	 * @return the current Geofence level
	 */
	public int getCurrentGeoFenceLevel() {
		return currentGeoFenceLevel;
	}

	/**
	 * @param currentGeoFenceLevel
	 *            the current Geofence level which has to be set
	 */
	public void setCurrentGeoFenceLevel(int currentGeoFenceLevel) {
		this.currentGeoFenceLevel = currentGeoFenceLevel;
	}

	/**
	 * @return the area list
	 */
	public AreaList getAreaList() {
		return areaList;
	}

	/**
	 * @param areaList
	 *            the area list which has to be set
	 */
	public void setAreaList(AreaList areaList) {
		this.areaList = areaList;
	}

	/**
	 * @return the badge list
	 */
	public BadgeList getBadgeList() {
		return badgeList;
	}

	/**
	 * @param badgeList
	 *            the badge list which has to be set
	 */
	public void setBadgeList(BadgeList badgeList) {
		this.badgeList = badgeList;
	}

	/**
	 * @return <code>true</code> if the visitor mode is active; <code>false</code>
	 *         otherwise
	 */
	public boolean isGodModeActive() {
		return godModeActive;
	}

	/**
	 * @param godMode
	 *            the status of the visitor mode which has to be set
	 */
	public void setGodMode(boolean godMode) {
		this.godModeActive = godMode;
	}

	/**
	 * @return the X coordinate of the robot position
	 */
	public double getRobotPositionX() {
		return robotPositionX;
	}

	/**
	 * @param robotPositionX
	 *            the X coordinate of the robot position which has to be set
	 */
	public void setRobotPositionX(double robotPositionX) {
		this.robotPositionX = robotPositionX;
	}

	/**
	 * @return the Y coordinate of the robot position
	 */
	public double getRobotPositionY() {
		return robotPositionY;
	}

	/**
	 * @param robotPositionY
	 *            the Y coordinate of the robot position which has to be set
	 */
	public void setRobotPositionY(double robotPositionY) {
		this.robotPositionY = robotPositionY;
	}

	/**
	 * @return the active area list
	 */
	public ArrayList<ActiveArea> getActiveAreasList() {
		return activeAreasList;
	}

	/**
	 * @param activeAreasList
	 *            the active area list which has to be set
	 */
	public void setActiveAreasList(ArrayList<ActiveArea> activeAreasList) {
		this.activeAreasList = activeAreasList;
	}

	/**
	 * @return the active badge list
	 */
	public List<ActiveBadge> getActiveBadgesList() {
		return activeBadgesList;
	}

	/**
	 * @param activeBadgesList
	 *            the active badge list which has to be set
	 */
	public void setActiveBadgesList(ArrayList<ActiveBadge> activeBadgesList) {
		this.activeBadgesList = activeBadgesList;
	}

	/**
	 * @return the nearest active area
	 */
	public ActiveArea getNearestActiveArea() {
		return nearestActiveArea;
	}

	/**
	 * @param nearestActiveArea
	 *            the nearest active area which has to be set
	 */
	public void setNearestActiveArea(ActiveArea nearestActiveArea) {
		this.nearestActiveArea = nearestActiveArea;
	}

	/**
	 * @return the current robot connection status:
	 *         <ul>
	 *         <li><code>ConnectionStatusType.OK</code> if the robot is connected
	 *         <li><code>ConnectionStatusType.PENDING</code> if the robot is
	 *         connecting
	 *         <li><code>ConnectionStatusType.NEW</code> if the robot is not
	 *         connected and there was no connection attempt until now
	 *         <li><code>ConnectionStatusType.ERROR</code> if the robot is not
	 *         connected and there was a failed connection attempt
	 *         </ul>
	 */
	public ConnectionStatusType getRobotConnectionStatus() {
		return robotConnectionStatus;
	}

	/**
	 * @param robotConnectionStatus
	 *            the robot connection status which has to be set:
	 *            <ul>
	 *            <li><code>ConnectionStatusType.OK</code> if the robot is connected
	 *            <li><code>ConnectionStatusType.PENDING</code> if the robot is
	 *            connecting
	 *            <li><code>ConnectionStatusType.NEW</code> if the robot is not
	 *            connected and there was no connection attempt until now
	 *            <li><code>ConnectionStatusType.ERROR</code> if the robot is not
	 *            connected and there was a failed connection attempt
	 *            </ul>
	 */
	public void setRobotConnectionStatus(ConnectionStatusType robotConnectionStatus) {
		setChanged();
		this.robotConnectionStatus = robotConnectionStatus;
		notifyObservers();
	}

	/**
	 * @return the current RTLS connection status:
	 *         <ul>
	 *         <li><code>ConnectionStatusType.OK</code> if the RTLS is connected
	 *         <li><code>ConnectionStatusType.PENDING</code> if the RTLS is
	 *         connecting
	 *         <li><code>ConnectionStatusType.NEW</code> if the RTLS is not
	 *         connected and there was no connection attempt until now
	 *         <li><code>ConnectionStatusType.ERROR</code> if the RTLS is not
	 *         connected and there was a failed connection attempt
	 *         </ul>
	 */
	public ConnectionStatusType getRTLSConnectionStatus() {
		return rtlsConnectionStatus;
	}

	/**
	 * @param rtlsConnectionStatus
	 *            the RTLS connection status which has to be set:
	 *            <ul>
	 *            <li><code>ConnectionStatusType.OK</code> if the RTLS is connected
	 *            <li><code>ConnectionStatusType.PENDING</code> if the RTLS is
	 *            connecting
	 *            <li><code>ConnectionStatusType.NEW</code> if the RTLS is not
	 *            connected and there was no connection attempt until now
	 *            <li><code>ConnectionStatusType.ERROR</code> if the RTLS is not
	 *            connected and there was a failed connection attempt
	 *            </ul>
	 */
	public void setRTLSConnectionStatus(ConnectionStatusType rtlsConnectionStatus) {
		setChanged();
		this.rtlsConnectionStatus = rtlsConnectionStatus;
		notifyObservers();
	}

	/**
	 * @return the current light module connection status:
	 *         <ul>
	 *         <li><code>ConnectionStatusType.OK</code> if the light module is
	 *         connected
	 *         <li><code>ConnectionStatusType.PENDING</code> if the light module is
	 *         connecting
	 *         <li><code>ConnectionStatusType.NEW</code> if the light module is not
	 *         connected and there was no connection attempt until now
	 *         <li><code>ConnectionStatusType.ERROR</code> if the light module is
	 *         not connected and there was a failed connection attempt
	 *         </ul>
	 */
	public ConnectionStatusType getLightConnectionStatus() {
		return lightConnectionStatus;
	}

	/**
	 * @param lightConnectionStatus
	 *            the light module connection status which has to be set:
	 *            <ul>
	 *            <li><code>ConnectionStatusType.OK</code> if the light module is
	 *            connected
	 *            <li><code>ConnectionStatusType.PENDING</code> if the light module
	 *            is connecting
	 *            <li><code>ConnectionStatusType.NEW</code> if the light module is
	 *            not connected and there was no connection attempt until now
	 *            <li><code>ConnectionStatusType.ERROR</code> if the light module is
	 *            not connected and there was a failed connection attempt
	 *            </ul>
	 */
	public void setLightConnectionStatus(ConnectionStatusType lightConnectionStatus) {
		setChanged();
		this.lightConnectionStatus = lightConnectionStatus;
		notifyObservers();
	}

	/**
	 * @return <code>true</code> if the robot is currently reconnecting;
	 *         <code>false</code> otherwise
	 */
	public boolean isRobotReconnecting() {
		return isRobotReconnecting;
	}

	/**
	 * @param isRobotReconnecting
	 *            the reconnection status of the robot connection which has to be
	 *            set
	 */
	public void setRobotReconnecting(boolean isRobotReconnecting) {
		this.isRobotReconnecting = isRobotReconnecting;
	}

	/**
	 * @return <code>true</code> if the RTLS is currently reconnecting;
	 *         <code>false</code> otherwise
	 */
	public boolean isRTLSReconnecting() {
		return isRTLSReconnecting;
	}

	/**
	 * @param isRTLSReconnecting
	 *            the reconnection status of the RTLS connection which has to be set
	 */
	public void setRTLSReconnecting(boolean isRTLSReconnecting) {
		this.isRTLSReconnecting = isRTLSReconnecting;
	}

	/**
	 * @return <code>true</code> if the light module is currently reconnecting;
	 *         <code>false</code> otherwise
	 */
	public boolean isLightReconnecting() {
		return isLightReconnecting;
	}

	/**
	 * @param isLightReconnecting
	 *            the reconnection status of the light module connection which has
	 *            to be set
	 */
	public void setLightReconnecting(boolean isLightReconnecting) {
		this.isLightReconnecting = isLightReconnecting;
	}
}
