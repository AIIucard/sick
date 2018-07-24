package main.htw.services;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;
import main.htw.database.SickDatabase;
import main.htw.datamodell.ActiveArea;
import main.htw.datamodell.ActiveBadge;
import main.htw.datamodell.RoleType;
import main.htw.handler.DMNHandler;
import main.htw.handler.LightHandler;
import main.htw.handler.RobotHandler;
import main.htw.manager.AreaManager;
import main.htw.manager.BadgeManager;
import main.htw.utils.ConnectionStatusType;
import main.htw.utils.SickColor;

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
				RoleType previousHighestRoleType = null;
				String eventType = (String) payload.get("eventType");
				ActiveBadge activeBadge = BadgeManager.getActiveBadgeByAddress((String) payload.get("address"));
				ActiveArea activeAreaToChange = AreaManager
						.getActiveAreaByID(Integer.parseInt(String.valueOf(payload.get("areaId"))));

				// Save highest role type for god mode is changed
				if (database.isGodModeActive()) {
					if (database.getNearestActiveArea() != null) {
						previousHighestRoleType = database.getNearestActiveArea().getHighestRoleType();
					}
				}

				switch (eventType) {
				case "IN":

					// Check if badge is registered in SickDatabase
					if (activeBadge != null) {
						ActiveArea activeAreaWithBadge = addBadgeToActiveArea(activeBadge, activeAreaToChange);
						if (activeAreaWithBadge != null) {
							isChanged = updateNearestActiveAreaIN(activeBadge, activeAreaWithBadge);
						} else {
							log.error("Could not add badge to ActiveArea " + activeAreaToChange.getArea().getName()
									+ "!");
						}
					} else {
						log.info("Register badge with name:" + payload.get("customName") + " and address: "
								+ payload.get("address"));
						addBadgeToActiveBadges(payload);
					}
					break;

				case "OUT":

					// Check if badge is registered in SickDatabase
					if (activeBadge != null) {
						ActiveArea activeAreaWithoutBadge = removeBadgeFromActiveArea(activeBadge, activeAreaToChange);
						isChanged = updateNearestActiveAreaOUT(activeBadge, activeAreaWithoutBadge);
					} else {
						log.info("Register badge with name:" + payload.get("customName") + " and address: "
								+ payload.get("address"));
						addBadgeToActiveBadges(payload);
					}

					// Check for roleChange at out event because highest role is changed for adding
					// batches to ActiveArea and not for removing them...
					if (database.isGodModeActive()) {
						if (!database.getNearestActiveArea().getHighestRoleType().equals(previousHighestRoleType)) {
							isChanged = true;
						}
					}
					break;

				default:
					log.error("Event " + eventType + " is not supported!");
					break;
				}

				if (database.getRobotConnectionStatus() == ConnectionStatusType.ERROR
						&& !database.isRobotReconnecting()) {
					RobotReconnectService robotService = new RobotReconnectService(database);
					robotService.startTheService();
					database.setRobotReconnecting(true);
				}

				if (database.getLightConnectionStatus() == ConnectionStatusType.ERROR
						&& database.isLightReconnecting()) {
					LightReconnectService lightService = new LightReconnectService(database);
					lightService.startTheService();
					database.setLightReconnecting(true);
				}

				// If Reconnect is successful update NearestActiveArea again
				if (database.isRobotReconnecting() || database.isLightReconnecting()) {
					if (database.getRobotConnectionStatus() == ConnectionStatusType.OK) {
						database.setRobotReconnecting(false);
					} else if (database.getLightConnectionStatus() == ConnectionStatusType.OK) {
						database.setLightReconnecting(false);
					} else {
						log.info("Reconnect is not completed! Connection status of the robot is: "
								+ database.getRobotConnectionStatus() + " and connection status of the light is: "
								+ database.getLightConnectionStatus() + "!");
					}
				}

				if (isChanged) {
					ActiveArea nearestActiveArea = database.getNearestActiveArea();
					if (nearestActiveArea == null) {
						if (database.getRobotConnectionStatus() == ConnectionStatusType.OK) {
							RobotHandler.getInstance().sendSecurityLevel(10);
							log.info("Set speedLvl: " + 10 + " and light color: " + SickColor.WHITE);
						} else {
							log.info("Can not set robot SpeedLvl due to missing coonnection to robot!");
						}
						if (database.getLightConnectionStatus() == ConnectionStatusType.OK) {
							LightHandler.getInstance().setLight(SickColor.WHITE);
						} else {
							log.info("Can not set light SickColor due to missing coonnection to light!");
						}
					} else {
						Pair<Integer, SickColor> decision = null;

						if (database.isGodModeActive()) {
							decision = DMNHandler.getInstance().evaluateDecision(
									nearestActiveArea.getHighestRoleType().toString(), nearestActiveArea.getLevel());
						} else {
							RoleType lowestRole = AreaManager.getLowestRoleInActiveArea(nearestActiveArea);
							decision = DMNHandler.getInstance().evaluateDecision(lowestRole.toString(),
									nearestActiveArea.getLevel());
						}
						if (decision != null) {
							RobotHandler.getInstance().sendSecurityLevel(decision.getKey().intValue());
							LightHandler.getInstance().setLight(decision.getValue());
							log.info("SpeedLvl: " + decision.getKey().intValue() + " Light: " + decision.getValue());
						} else {
							log.error("No Decision available! Can not change robot speed and ligth!");
						}
					}
				}
				return null;
			}
		};

	}

	private void addBadgeToActiveBadges(JSONObject payload) {
		// TODO: Fabi & Review Maxi
		log.warn("Not implemented! Fabe?");
	}

	private ActiveArea addBadgeToActiveArea(ActiveBadge badge, ActiveArea activeAreaToChange) {
		return AreaManager.addActiveBadgeToActiveArea(badge, activeAreaToChange);
	}

	private ActiveArea removeBadgeFromActiveArea(ActiveBadge badge, ActiveArea activeAreaToChange) {
		return AreaManager.removeActiveBadgeFromActiveArea(badge, activeAreaToChange);
	}

	private boolean updateNearestActiveAreaIN(ActiveBadge badge, ActiveArea activeAreaWithBadge) {
		return AreaManager.updateNearestActiveAreaIN(badge, activeAreaWithBadge);
	}

	private boolean updateNearestActiveAreaOUT(ActiveBadge badge, ActiveArea activeAreaWithoutBadge) {
		return AreaManager.updateNearestActiveAreaOUT(badge, activeAreaWithoutBadge);
	}
}
