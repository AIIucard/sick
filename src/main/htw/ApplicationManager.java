package main.htw;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import main.htw.utils.ConnectionStatusType;

public class ApplicationManager {

	private boolean isRunning = false;

	private static Object lock = new Object();
	private static ApplicationManager instance = null;

	private static Thread t = null;
	private static BusinessLogicThread logic = null;
	private static RobotConnectionService robotService = null;
	private static RTLSConnectionService rtlsService = null;
	private static LightConnectionService lightService = null;
	private SickApplication app = null;

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

			log.info("Create service to get all active badges...");
			Service<Void> getAllActiveBadgesService = new Service<Void>() {
				@Override
				protected Task<Void> createTask() {
					return new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							try {
								latch.await();
								if (database.getRTLSConnectionStatus() == ConnectionStatusType.OK) {
									RTLSHandler rtlsConnectionHandler = RTLSHandler.getInstance();
									rtlsConnectionHandler.getActiveBadges();
								} else {
									log.info("Can not get active badges from RTLS system! Connection not available!");
									if (isRunning) {
										stopApplication();
									}
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

			log.info("Create service to update Areas...");
			Service<Void> getAllAreasService = new Service<Void>() {
				@Override
				protected Task<Void> createTask() {
					return new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							try {
								latch.await();
								if (database.getRTLSConnectionStatus() == ConnectionStatusType.OK) {
									RTLSHandler rtlsConnectionHandler = RTLSHandler.getInstance();
									rtlsConnectionHandler.updateAreas();
									// TODO Continue
								} else {
									log.info("Can not get active badges from RTLS system! Connection not available!");
									if (isRunning) {
										stopApplication();
									}
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
			getAllAreasService.start();

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
		app.getStartButton().setDisable(false);
		app.getStopButton().setDisable(true);
	}

	public boolean isRunning() {
		return isRunning;
	}
}