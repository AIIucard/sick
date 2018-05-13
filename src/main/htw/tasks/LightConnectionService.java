package main.htw.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.htw.database.SickDatabase;
import main.htw.utils.ConnectionStatusType;

public class LightConnectionService extends Service<Void> {

	private SickDatabase database = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public LightConnectionService(SickDatabase database) {
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
			log.info("LightService canceled!");
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
		database.setLightConnectionStatus(ConnectionStatusType.NEW);
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				// create other variables here
				while (isRunning()) {
					log.info("Light Service Alive");
					Thread.sleep(1000);
				}
				return null;
			}
		};
	}
}
