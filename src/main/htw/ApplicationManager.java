package main.htw;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.threads.BusinessLogicThread;

public class ApplicationManager {

	private boolean isRunning = false;

	private static Object lock = new Object();
	private static ApplicationManager instance = null;

	private static Thread t = null;
	private static BusinessLogicThread logic = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private ApplicationManager() {

	}

	public static ApplicationManager getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new ApplicationManager();
				}
			}
		}
		return (instance);
	}

	public void startApplication() {
		if (!isRunning) {
			isRunning = true;
			logic = new BusinessLogicThread();
			t = new Thread(logic, "SickBusinessLogic");
			t.start();
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