package main.htw;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.database.SickDatabase;
import main.htw.handler.LightConnectionHandler;
import main.htw.handler.RTLSConnectionHandler;
import main.htw.handler.RobotConnectionHandler;
import main.htw.services.LightConnectionService;
import main.htw.services.RTLSConnectionService;
import main.htw.services.RobotConnectionService;
import main.htw.threads.BusinessLogicThread;

public class ApplicationManager {

	private boolean isRunning = false;

	private static Object lock = new Object();
	private static ApplicationManager instance = null;

	private static Thread t = null;
	private static BusinessLogicThread logic = null;
	private static RobotConnectionService robotService = null;
	private static RTLSConnectionService rtlsService = null;
	private static LightConnectionService lightService = null;

	private static SickDatabase database = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private ApplicationManager() {

	}

	public static ApplicationManager getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new ApplicationManager();
					database = SickDatabase.getInstance();
				}
			}
		}
		return (instance);
	}

	public void startApplication() {
		if (!isRunning) {
			isRunning = true;
			robotService = new RobotConnectionService(database);
			robotService.startTheService();

			// rtlsService = new RTLSConnectionService(database);
			// rtlsService.startTheService();
			//
			// lightService = new LightConnectionService(database);
			// lightService.startTheService();

			logic = new BusinessLogicThread(database);
			t = new Thread(logic, "SickBusinessLogic");
			t.start();
			log.info("started");

		} else {
			log.debug("Tried to start new thread but application is already running!");
		}
	}

	public void stopApplication() {
		isRunning = false;
		if (t != null && logic != null) {

			robotService.cancel();
			rtlsService.cancel();
			lightService.cancel();
			logic.terminate();
			RTLSConnectionHandler.setStatusNew();
			RobotConnectionHandler.setStatusNew();
			LightConnectionHandler.setStatusNew();
		}
	}

	public boolean isRunning() {
		return isRunning;
	}
}