package main.htw.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.htw.database.SickDatabase;
import main.htw.handler.RTLSConnectionHandler;
import main.htw.utils.ConnectionStatusType;

public class RTLSConnectionService extends Service<Void> {

	private SickDatabase database = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public RTLSConnectionService(SickDatabase database) {
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
		database.setRTLSConnectionStatus(ConnectionStatusType.NEW);
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				database.setRTLSConnectionStatus(ConnectionStatusType.PENDING);
				RTLSConnectionHandler rtlsConnectionHandler = RTLSConnectionHandler.getInstance();
				try {
					rtlsConnectionHandler.initializeConnection();
					database.setRTLSConnectionStatus(ConnectionStatusType.OK);
				} catch (Exception ex) {
					log.error("Exception thrown: " + ex.getLocalizedMessage());
					database.setRTLSConnectionStatus(ConnectionStatusType.ERROR);
				}
				return null;
			}
		};
	}
}