package main.htw.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.database.SickDatabase;

public class BusinessLogicThread implements Runnable {

	private boolean isStarted = true;
	private volatile boolean running = true;
	private static Thread t = null;
	private SickDatabase database = null;

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

	public void terminate() {
		running = false;
	}
}
