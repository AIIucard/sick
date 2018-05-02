package main.htw.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.properties.CFGPropertyManager;
import main.htw.utils.ConnectionStatusType;

public class SickConnectionHandler {

	protected static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	protected static CFGPropertyManager propManager = null;

	protected static ConnectionStatusType connectionStatus;

	public static ConnectionStatusType getConnectionStatus() {
		return connectionStatus;
	}

	public static void setStatusOK() {
		connectionStatus = ConnectionStatusType.OK;
	}

	public static void setStatusPending() {
		connectionStatus = ConnectionStatusType.PENDING;
	}

	public static void setStatusNew() {
		connectionStatus = ConnectionStatusType.NEW;
	}

	public static void setStatusError() {
		connectionStatus = ConnectionStatusType.ERROR;
	}
}
