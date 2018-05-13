package main.htw.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.htw.database.SickDatabase;
import main.htw.utils.ConnectionStatusType;

public class RTLSConnectionService extends Service<Void> {

	private SickDatabase database = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public RTLSConnectionService(SickDatabase database) {
		this.database = database;

		// if succeeded
		setOnSucceeded(s -> {
			// code if Service succeeds
		});

		// if failed
		setOnFailed(fail -> {
			// code it Service fails
		});

		// if cancelled
		setOnCancelled(cancelled -> {
			log.info("RTLSService canceled!");
		});
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

				while (isRunning()) {
					log.info("RTLS Service Alive");
					Thread.sleep(1000);
				}
				return null;
			}
		};
	}
}
