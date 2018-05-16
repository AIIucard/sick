package main.htw.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.xml.bind.JAXBException;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.datamodell.ActiveArea;
import main.htw.datamodell.ActiveBadge;
import main.htw.utils.ConnectionStatusType;
import main.htw.xml.AreaList;
import main.htw.xml.Badge;
import main.htw.xml.BadgeList;
import main.htw.xml.XMLMarshler;

public class SickDatabase extends Observable {

	// TODO Remove logger
	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());
	private int currentGeoFenceLevel = -1;
	private static AreaList areaList = null;
	private static BadgeList badgeList = new BadgeList();
	private List<ActiveBadge> activeBadges = new ArrayList<ActiveBadge>();
	private static boolean godModeActive = false;
	private static XMLMarshler xmlMarshaller = null;

	private double robotPositionX = 0;
	private double robotPositionY = 0;

	private ConnectionStatusType robotConnectionStatus = ConnectionStatusType.NEW;
	private ConnectionStatusType rtlsConnectionStatus = ConnectionStatusType.NEW;
	private ConnectionStatusType lightConnectionStatus = ConnectionStatusType.NEW;

	private ArrayList<ActiveArea> activeAreaList = new ArrayList<ActiveArea>();

	private static Object lock = new Object();
	private static SickDatabase instance = null;

	private SickDatabase() {
		// Use getInstance
	}

	public void createActiveBadges(JSONObject jsonObject) {

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

	public boolean isGodModeSet() {
		return godModeActive;
	}

	public void setGodMode(boolean godModeActive) {
		SickDatabase.godModeActive = godModeActive;
	}

	public int getCurrentGeoFenceLevel() {
		return currentGeoFenceLevel;
	}

	public void setCurrentGeoFenceLevel(int currentGeoFenceLevel) {
		this.currentGeoFenceLevel = currentGeoFenceLevel;
	}

	// TODO Remove try catch + logger
	public Badge getBadgeByAddress(String address) {
		try {
			Badge badge = SickDatabase.badgeList.getBadgeByAddress(address);
			return badge;
		} catch (Exception e) {
			log.error("No such badge registered in Database!");
			return null;
		}
	}

	public ActiveBadge getActiveBadgeByAddress(String address) {

		// List<ActiveBadges>
		for (ActiveBadge a : activeBadges) {
			if (a.getAddress().equals(address))
				return a;
		}

		return null;
	}

	public BadgeList getBadgeList() {
		return badgeList;
	}

	public void setBadgeList(BadgeList badgeList) {
		SickDatabase.badgeList = badgeList;
	}

	// TODO Remove try catch + logger
	public void addToBadgeList(Badge badge) {
		if (badgeList == null) {
			badgeList = new BadgeList();
		}

		SickDatabase.badgeList.addBadge(badge);

		try {
			xmlMarshaller = XMLMarshler.getInstance();
			if (xmlMarshaller != null && badgeList != null) {
				xmlMarshaller.marshalBadgeList(badgeList);
			} else {
				log.error("Cannot store badges!");
			}
		} catch (JAXBException e) {
			log.error("Cannot store badges! JAXBException thrown: " + e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeFromBadgeList(Badge badge) {
		SickDatabase.badgeList.removeBadge(badge);
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
		this.robotPositionX = robotPositionX;
	}

	public double getRobotPositionY() {
		return robotPositionY;
	}

	public void setRobotPositionY(double robotPositionY) {
		this.robotPositionY = robotPositionY;
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
