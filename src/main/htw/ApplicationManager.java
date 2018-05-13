package main.htw;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.htw.database.SickDatabase;
import main.htw.handler.RTLSHandler;
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

			ExecutorService executorService = Executors.newCachedThreadPool();
			robotService = new RobotConnectionService(database);
			robotService.setExecutor(executorService);
			robotService.startTheService();

			rtlsService = new RTLSConnectionService(database);
			rtlsService.setExecutor(executorService);
			rtlsService.startTheService();

			lightService = new LightConnectionService(database);
			lightService.setExecutor(executorService);
			lightService.startTheService();
			executorService.shutdown();

			log.info("Create service to get all active badges...");
			Service<Void> getAllActiveBadgesService = new Service<Void>() {
				@Override
				protected Task<Void> createTask() {
					return new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							try {
								boolean finshed = executorService.awaitTermination(1, TimeUnit.MINUTES);
								if (finshed) {
									RTLSHandler rtlsConnectionHandler = RTLSHandler.getInstance();
									rtlsConnectionHandler.getActiveBadges();
								}
							} catch (InterruptedException e) {
								log.error("Cannot load active Badges! InterruptedException thrown: "
										+ e.getLocalizedMessage());
							}
							return null;
						}
					};
				}
			};
			getAllActiveBadgesService.start();

			// getAllActiveBadges

			// getAllAreas

			// BusinessLogic

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
			logic.terminate();
		}
	}

	public boolean isRunning() {
		return isRunning;
	}
}