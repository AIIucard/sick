package main.htw.handler;

import java.io.IOException;

import main.htw.utils.ConnectionStatusType;

public class LightHandler {

	private static Object lock = new Object();
	private static LightHandler instance = null;

	private static ConnectionStatusType connectionStatus;

	private LightHandler() {
		// Use getInstance
	}

	public static LightHandler getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new LightHandler();
				}
			}
		}
		return (instance);
	}

	public static ConnectionStatusType getConnectionStatus() {
		return connectionStatus;
	}

	public static void setStatusOK() {
		connectionStatus = ConnectionStatusType.ALIVE;
	}

	public static void setStatusPending() {
		connectionStatus = ConnectionStatusType.CONNECTING;
	}

	public static void setStatusError() {
		connectionStatus = ConnectionStatusType.DEAD;
	}
}
