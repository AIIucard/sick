package main.htw.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.database.SickDatabase;
import main.htw.datamodell.ActiveBadge;
import main.htw.messages.MessageGeoFence;
import main.htw.utils.SickUtils;

public class BusinessLogicThread implements Runnable {

	private boolean isStarted = true;
	private static Thread t = null;
	private SickDatabase database = null;
	private volatile boolean running;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public BusinessLogicThread(SickDatabase database) {
		if (database != null) {
			this.database = database;
		} else {
			log.error("No Database was found! Can not start BusinessLogicThread!");
		}
	}

	@Override
	public void run() {
		if (isStarted) {
			initializeConnections();
		}
	}

	private void initializeConnections() {
		try {
			// log.info("Connecting to RTLS...");
			// RTLSConnectionHandler rtlsConnectionHandler =
			// RTLSConnectionHandler.getInstance();
			// log.info("Connected to RTLS!");
			// log.info("Connecting to Robot...");
			// RobotConnectionHandler robotHandler = RobotConnectionHandler.getInstance();
			// log.info("Connected to Robot!");
			// log.info("Connecting to Light...");
			// LightConnectionHandler lightHandler = LightConnectionHandler.getInstance();
			// log.info("Connected to Light!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sortAreas() {
		// hole dir aus database get areaList

		// sortiere Liste nach distance to Robot und schreibe das in ein Keyvalue
		// Store(ID,geofence

	}

	public void computeRTLSMessage(MessageGeoFence messageGeoFence) {
		//
		// ermittle den betroffenen Badge und hole ihn dir aus der Datenbank
		ActiveBadge activeBadge = SickUtils.getActiveBadgeByAddress(messageGeoFence.getAddress());
		//

		// setze den aktuellen Geofence
		activeBadge.setCurrentGeoFence(messageGeoFence.getAreasId());

		// call die Funktion die prüft ob der Badge gerade am nächsten dran ist
	}

	public void terminate() {
		running = false;
	}
}
