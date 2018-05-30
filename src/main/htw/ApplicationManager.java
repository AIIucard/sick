package main.htw;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.database.SickDatabase;
import main.htw.services.BusinessLogicService;
import main.htw.services.InitializationService;
import main.htw.services.LightConnectionService;
import main.htw.services.RTLSConnectionService;
import main.htw.services.RobotConnectionService;

public class ApplicationManager {

	private boolean isRunning = false;

	private static Object lock = new Object();
	private static ApplicationManager instance = null;

	private static RobotConnectionService robotService = null;
	private static RTLSConnectionService rtlsService = null;
	private static LightConnectionService lightService = null;
	private static InitializationService initializationService = null;
	private SickApplication app = null;

	private static SickDatabase database = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private ApplicationManager() {

	}

	public static ApplicationManager getInstance() {
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

	public void startApplication(SickApplication app) {
		this.app = app;
		if (!isRunning) {
			isRunning = true;

			ExecutorService executorService = Executors.newCachedThreadPool();
			CountDownLatch latch = new CountDownLatch(3);

			robotService = new RobotConnectionService(database, latch);
			robotService.setExecutor(executorService);
			robotService.startTheService();

			rtlsService = new RTLSConnectionService(database, latch);
			rtlsService.setExecutor(executorService);
			rtlsService.startTheService();

			lightService = new LightConnectionService(database, latch);
			lightService.setExecutor(executorService);
			lightService.startTheService();
			executorService.shutdown();

			initializationService = new InitializationService(database, latch);
			initializationService.startTheService();
		} else {
			log.debug("Tried to start new thread but application is already running!");
		}
	}

	public void handleMessage() {

	}

	public void stopApplication() {
		isRunning = false;
		app.getStartButton().setDisable(false);
		app.getStopButton().setDisable(true);
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void handleGeofenceEvent(JSONObject payload) {
		BusinessLogicService businessLogicService = new BusinessLogicService(SickDatabase.getInstance(), payload);
		businessLogicService.start();
	}
}