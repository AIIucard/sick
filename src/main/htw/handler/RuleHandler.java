package main.htw.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.messages.MessageGeoFence;

public class RuleHandler implements IHandler {

	private static Object lock = new Object();
	private static RuleHandler instance = null;
	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public static RuleHandler getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new RuleHandler();
				}
			}
		}
		return (instance);
	}

	private RuleHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleConnection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleReply() {
		// TODO Auto-generated method stub

	}

	public void handleGeofenceIn(MessageGeoFence geoFence) {
		// TODO handle IN event
		log.warn("NOT IMPLEMENTED");
	}

	public void handleGeofenceOut(MessageGeoFence geoFence) {
		// TODO handle OUT event
		log.warn("NOT IMPLEMENTED");
	}
}
