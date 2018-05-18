package main.htw.services;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.htw.ApplicationManager;
import main.htw.database.SickDatabase;
import main.htw.handler.RTLSHandler;
import main.htw.utils.ConnectionStatusType;

public class InitializationService extends Service<Void> {

	private SickDatabase database = null;
	private CountDownLatch countDownLatch;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public InitializationService(SickDatabase database, CountDownLatch countDownLatch) {
		this.database = database;
		this.countDownLatch = countDownLatch;
	}

	public void startTheService() {
		if (!isRunning()) {
			reset();
			start();
		}
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				countDownLatch.await();
				if (database.getRTLSConnectionStatus() == ConnectionStatusType.OK
						&& database.getRobotConnectionStatus() == ConnectionStatusType.OK
						&& database.getLightConnectionStatus() == ConnectionStatusType.OK) {
					runGetAllActiveBadgesService();
					runUpdateAreasService();
					runRegisterForGeoFencingEventsService();
				} else {
					log.error("Initial connection test failed! Application will be terminated!");
					if (ApplicationManager.getInstance().isRunning()) {
						ApplicationManager.getInstance().stopApplication();
					}
				}
				return null;
			}
		};
	}

	private void runGetAllActiveBadgesService() {
		log.info("Create service to get all active badges...");
		Service<Void> getAllActiveBadgesService = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						if (database.getRTLSConnectionStatus() == ConnectionStatusType.OK) {
							RTLSHandler rtlsHandler = RTLSHandler.getInstance();
							rtlsHandler.getActiveBadges();
							log.info("Active Badges set!");
						} else {
							log.info("Can not get active badges from RTLS system! Connection not available!");
						}
						return null;
					}
				};
			}
		};
		getAllActiveBadgesService.start();
	}

	private void runUpdateAreasService() {
		log.info("Create service to update Areas...");
		Service<Void> getAllAreasService = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						if (database.getRTLSConnectionStatus() == ConnectionStatusType.OK) {
							RTLSHandler rtlsHandler = RTLSHandler.getInstance();
							rtlsHandler.updateAreas();
							// TODO Set Active Areas
							log.info("Areas updated!");
						} else {
							log.info("Can not get active badges from RTLS system! Connection not available!");
						}
						return null;
					}
				};
			}
		};
		getAllAreasService.start();
	}

	private void runRegisterForGeoFencingEventsService() {
		log.info("Create service to register for Geo Fencing Events...");
		Service<Void> registerForGeoFenceMessagesService = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						if (database.getRTLSConnectionStatus() == ConnectionStatusType.OK) {
							RTLSHandler rtlsHandler = RTLSHandler.getInstance();
							rtlsHandler.registerGeoFence();
							log.info("Registered for Geo Fencing Events!");
						} else {
							log.info("Can not get active badges from RTLS system! Connection not available!");

						}
						return null;
					}
				};
			}
		};
		registerForGeoFenceMessagesService.start();
	}
}
