package main.htw.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.htw.database.SickDatabase;
import main.htw.handler.LightHandler;
import main.htw.utils.ConnectionStatusType;
import main.htw.utils.SickColor;

public class LightReconnectService extends Service<Void> {

	private SickDatabase database = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public LightReconnectService(SickDatabase database) {
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
		database.setLightConnectionStatus(ConnectionStatusType.NEW);
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				database.setLightConnectionStatus(ConnectionStatusType.PENDING);
				LightHandler lightConnectionHandler = LightHandler.getInstance();
				try {
					lightConnectionHandler.setLight(SickColor.WHITE);
					database.setLightConnectionStatus(ConnectionStatusType.OK);
				} catch (Exception ex) {
					log.error("Exception thrown: " + ex.getLocalizedMessage());
					database.setLightConnectionStatus(ConnectionStatusType.ERROR);
				}
				return null;
			}
		};
	}
}
