package main.htw.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.htw.database.SickDatabase;
import main.htw.handler.RobotConnectionHandler;
import main.htw.utils.ConnectionStatusType;

public class RobotConnectionService extends Service<Void> {

	private SickDatabase database = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public RobotConnectionService(SickDatabase database) {
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
				RobotConnectionHandler robotConnectionHandler = RobotConnectionHandler.getInstance();
				try {
					robotConnectionHandler.initializeConnection();
					database.setRobotConnectionStatus(ConnectionStatusType.OK);
				} catch (Exception ex) {
					log.error("Exception thrown: " + ex.getLocalizedMessage());
					database.setRobotConnectionStatus(ConnectionStatusType.ERROR);
				}
				return null;
			}
		};
	}
}
