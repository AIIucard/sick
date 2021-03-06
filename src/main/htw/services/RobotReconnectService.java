package main.htw.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.htw.database.SickDatabase;
import main.htw.handler.RobotHandler;
import main.htw.utils.ConnectionStatusType;

public class RobotReconnectService extends Service<Void> {

	private SickDatabase database = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public RobotReconnectService(SickDatabase database) {
		this.database = database;
	}

	public void startTheService() {
		if (!isRunning()) {
			reset();
			start();
		}
	}

	@Override
	public void reset() {
		super.reset();
		database.setRobotConnectionStatus(ConnectionStatusType.NEW);
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				database.setRobotConnectionStatus(ConnectionStatusType.PENDING);
				RobotHandler robotConnectionHandler = RobotHandler.getInstance();
				try {
					robotConnectionHandler.initializeConnection();
				} catch (Exception ex) {
					log.error("Exception thrown: " + ex.getLocalizedMessage());
					database.setRobotConnectionStatus(ConnectionStatusType.ERROR);
				}
				return null;
			}
		};
	}
}
