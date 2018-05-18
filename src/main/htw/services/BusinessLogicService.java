package main.htw.services;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.htw.database.SickDatabase;
import main.htw.datamodell.ActiveBadge;
import main.htw.handler.LightHandler;
import main.htw.handler.RobotHandler;
import main.htw.utils.ConnectionStatusType;
import main.htw.utils.SickColor;
import main.htw.utils.SickUtils;

public class BusinessLogicService extends Service<Void> {

	private SickDatabase database = null;
	JSONObject payload = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public BusinessLogicService(SickDatabase database, JSONObject payload) {
		this.database = database;
		this.payload = payload;
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
				boolean isChanged = false;
				String eventType = (String) payload.get("eventType");
				ActiveBadge activeBadge = SickUtils.getActiveBadgeByAddress((String) payload.get("adress"));

				switch (eventType) {
				case "IN":

					// Check if badge is registered in SickDatabase
					if (activeBadge != null) {
						addBadgeToActiveArea(activeBadge);
						isChanged = updateNearestActiveAreaIN(activeBadge);
					} else {
						log.info("Register Badge with Name:" + payload.get("customName") + " and adress: "
								+ payload.get("adress"));
						addBadgeToActiveBadges(payload);
					}
					break;

				case "OUT":

					// Check if badge is registered in SickDatabase
					if (activeBadge != null) {
						removeBadgeFromActiveArea(activeBadge);
						isChanged = updateNearestActiveAreaOUT(activeBadge);
					} else {
						log.info("Register Badge with Name:" + payload.get("customName") + " and adress: "
								+ payload.get("adress"));
						addBadgeToActiveBadges(payload);
					}
					break;

				default:
					log.error("Event " + eventType + " is not supported!");
					break;
				}

				if (database.getRobotConnectionStatus() == ConnectionStatusType.OK) {
					// TODO: Implement Reconnect Service
					database.setRobotReconnected(true);
				}

				if (database.getLightConnectionStatus() == ConnectionStatusType.OK) {
					// TODO: Implement Reconnect Service
					database.setLightReconnected(true);
				}

				if (database.isRobotReconnected() || database.isLightReconnected()) {
					if (database.getRobotConnectionStatus() == ConnectionStatusType.OK
							&& database.getLightConnectionStatus() == ConnectionStatusType.OK) {
						database.setRobotReconnected(false);
						database.setLightReconnected(false);
						isChanged = updateNearestActiveArea(activeBadge);
					}
				}

				if (isChanged) {
					// RETURN for DMNHandler
					// DMNHandler.getInstance().evaluateDecision(role, geofence);
					// Parse Decision
					LightHandler.getInstance().setLight(SickColor.BLUE.toString());
					RobotHandler.getInstance().sendSecurityLevel(-1);
				}
				return null;
			}
		};
	}

	private void addBadgeToActiveBadges(JSONObject payload) {
		// TODO: Fabi
		log.warn("Not implemented! Fabe?");
	}

	private void addBadgeToActiveArea(ActiveBadge badge) {

	}

	private void removeBadgeFromActiveArea(ActiveBadge badge) {

	}

	private boolean updateNearestActiveAreaIN(ActiveBadge badge) {
		// Check for role!
		// TODO: Implement
		return false;
	}

	private boolean updateNearestActiveAreaOUT(ActiveBadge badge) {
		// Check for role!
		// TODO: Implement
		return false;
	}

	private boolean updateNearestActiveArea(ActiveBadge badge) {
		// Check for role!
		// TODO: Implement
		return false;
	}
}
