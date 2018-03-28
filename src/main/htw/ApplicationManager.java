package main.htw;

import java.io.IOException;

public class ApplicationManager {

	private boolean isRunnig = false;

	private static Object lock = new Object();
	private static ApplicationManager instance = null;

	private static Thread t = null;
	private static BusinessLogic logic = null;

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
		isRunnig = true;
		logic = new BusinessLogic();
		t = new Thread(logic);
		t.start();
	}

	public void stopApplication() {
		isRunnig = false;
		if (t != null && logic != null) {
			logic.terminate();
		}
	}

	public boolean isRunning() {
		return isRunnig;
	}
}