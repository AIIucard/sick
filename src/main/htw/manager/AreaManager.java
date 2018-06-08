package main.htw.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.database.SickDatabase;
import main.htw.datamodell.ActiveArea;
import main.htw.datamodell.ActiveAreaComparator;
import main.htw.datamodell.ActiveBadge;
import main.htw.datamodell.RoleType;
import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.utils.SickUtils;
import main.htw.xml.Area;
import main.htw.xml.AreaList;
import main.htw.xml.Coordinate;
import main.htw.xml.Shape;

public class AreaManager {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private static int getSickArea() {
		int sickPosArea = -1;
		CFGPropertyManager propManager = CFGPropertyManager.getInstance();
		sickPosArea = Integer.parseInt(propManager.getProperty(PropertiesKeys.AREA_LAYER));
		return sickPosArea;
	}

	private static Integer getNextId() {
		int highestID = -1;
		SickDatabase database = SickDatabase.getInstance();
		List<Area> areas = database.getAreaList().getAreas();
		for (Area area : areas) {
			if (area.getId() > highestID) {
				highestID = area.getId();
			}
		}
		return (highestID + 1);
	}

	public static void addAreaToActiveArea(Area area, int level) {
		log.warn("AddAreaToActiveArea - not implemented!");
		return;
		// SickDatabase db = SickDatabase.getInstance();
		// List<ActiveArea> activeAreas = new ArrayList<>();
		// activeAreas = db.getActiveAreasList();
		//
		// ActiveArea activeArea = new ActiveArea(area, level);
		// sort by distance
		// TODO: Implement, Sort Active Area List by Level
	}

	public static ActiveArea addActiveBadgeToActiveArea(ActiveBadge activeBadge, ActiveArea activeArea) {
		ActiveArea activeAreaWithBadge = null;
		SickDatabase database = SickDatabase.getInstance();
		ArrayList<ActiveArea> activeAreasList = database.getActiveAreasList();
		for (ActiveArea currentActiveArea : activeAreasList) {
			if (currentActiveArea.equals(activeArea)) {

				// Update Badge List
				currentActiveArea.addActiveBadge(activeBadge);
				activeAreaWithBadge = currentActiveArea;

				// Update Highest Role
				RoleType role = activeBadge.getRole();
				if (role.equals(RoleType.PROFESSOR)) {
					currentActiveArea.setHighestRoleType(RoleType.PROFESSOR);
				} else if (role.equals(RoleType.LABORANT)
						&& !(currentActiveArea.getHighestRoleType().equals(RoleType.PROFESSOR))) {
					currentActiveArea.setHighestRoleType(RoleType.LABORANT);
				}
			}
		}
		return activeAreaWithBadge;
	}

	public static ActiveArea removeActiveBadgeFromActiveArea(ActiveBadge activeBadge, ActiveArea activeArea) {
		ActiveArea activeAreaWithoutBadge = null;
		SickDatabase database = SickDatabase.getInstance();
		ArrayList<ActiveArea> activeAreasList = database.getActiveAreasList();
		for (ActiveArea currentActiveArea : activeAreasList) {
			if (currentActiveArea.equals(activeArea)) {

				// Count Roles
				int laborantNumber = 0;
				int professorNumber = 0;
				for (ActiveBadge currentActiveBadge : currentActiveArea.getContaingBatchesList()) {

					if (currentActiveBadge.getRole().equals(RoleType.PROFESSOR)) {
						professorNumber += 1;
					} else if (currentActiveBadge.getRole().equals(RoleType.LABORANT)) {
						laborantNumber += 1;
					}
				}

				// Update Highest Role
				RoleType role = activeBadge.getRole();
				if (activeBadge.getRole().equals(RoleType.PROFESSOR) && professorNumber == 1) {
					if (laborantNumber > 0) {
						currentActiveArea.setHighestRoleType(RoleType.LABORANT);
					} else {
						currentActiveArea.setHighestRoleType(RoleType.VISITOR);
					}
				} else if (role.equals(RoleType.LABORANT) && laborantNumber == 1) {
					currentActiveArea.setHighestRoleType(RoleType.VISITOR);
				}

				// Update Badge List
				currentActiveArea.removeActiveBadge(activeBadge);
				activeAreaWithoutBadge = currentActiveArea;
				break;
			}
		}
		return activeAreaWithoutBadge;
	}

	public static Area getAreaByID(Long id) {
		SickDatabase database = SickDatabase.getInstance();
		List<Area> areas = database.getAreaList().getAreas();

		for (Area area : areas) {
			if (area.getId() == id.intValue())
				return area;
		}

		log.error("Area with ID '" + id + "' not found!");
		return null;
	}

	public static ActiveArea getActiveAreaByID(Integer id) {
		SickDatabase database = SickDatabase.getInstance();
		List<ActiveArea> activeAreas = database.getActiveAreasList();

		for (ActiveArea activeArea : activeAreas) {
			if (activeArea.getArea().getId() == id)
				return activeArea;
		}

		log.error("Active Area with ID '" + id + "' not found!");
		return null;
	}

	public static boolean checkIfActiveAreaExistsByID(int id) {
		SickDatabase database = SickDatabase.getInstance();
		List<ActiveArea> activeAreas = database.getActiveAreasList();
		for (ActiveArea activeArea : activeAreas) {
			if (activeArea.getArea().getId().intValue() == id) {
				return true;
			}
		}
		return false;
	}

	public static Area addNewArea(String areaName, Double distanceToRobot) {
		log.info("Create new Area...");
		SickDatabase database = SickDatabase.getInstance();
		double robotPositionX = database.getRobotPositionX();
		double robotPositionY = database.getRobotPositionY();
		List<Area> areaList = database.getAreaList().getAreas();

		List<Coordinate> coordinates = SickUtils.calculateCoordinates(robotPositionX, robotPositionY, distanceToRobot);
		Shape newShape = new Shape("Polygon", coordinates);
		int sickPosArea = getSickArea();
		if (sickPosArea == -1) {
			return null;
		}

		Area newArea = new Area(getNextId(), areaName, sickPosArea, newShape, distanceToRobot);
		areaList.add(newArea);
		database.getAreaList().setAreas(areaList);
		log.info("Created new Area " + areaName + " with distance to Robot " + distanceToRobot);
		return newArea;
	}

	public static Area editArea(Area oldArea, String areaName, Double distanceToRobot) {
		log.info("Edit Area...");
		SickDatabase database = SickDatabase.getInstance();
		double robotPositionX = database.getRobotPositionX();
		double robotPositionY = database.getRobotPositionY();
		List<Area> areaList = database.getAreaList().getAreas();

		List<Coordinate> coordinates = SickUtils.calculateCoordinates(robotPositionX, robotPositionY, distanceToRobot);
		Shape newShape = new Shape("Polygon", coordinates);

		int pos = 0;

		for (int i = 0; i < areaList.size(); i++) {
			Area areaToCheck = areaList.get(i);
			if (areaToCheck.getName().equals(areaName) && areaToCheck.getDistanceToRobot().equals(distanceToRobot)) {
				pos = i;

				int sickPosArea = getSickArea();
				if (sickPosArea == -1) {
					return null;
				}

				Area updatedArea = new Area(areaList.get(pos).getId(), areaName, sickPosArea, newShape,
						distanceToRobot);
				areaList.set(pos, updatedArea);
				database.getAreaList().setAreas(areaList);
				log.info("Updated Area " + areaName + " with distance to Robot " + distanceToRobot);
				return updatedArea;
			}
		}
		return oldArea;
	}

	public static boolean isDublicatedArea(String oldName, Double oldDistance, String name, Double distance) {
		SickDatabase database = SickDatabase.getInstance();
		ArrayList<Area> areaList = new ArrayList<Area>();
		for (Area area : database.getAreaList().getAreas()) {
			if (!(area.getName().equals(oldName) && area.getDistanceToRobot().equals(oldDistance))) {
				areaList.add(area);
			}
		}

		for (Area area : areaList) {
			if (area.getName().equals(name) || area.getDistanceToRobot().equals(distance)) {
				return true;
			}
		}
		return false;
	}

	public static void removeArea(Area area) {
		log.info("Remove Area...");
		SickDatabase database = SickDatabase.getInstance();
		List<Area> areaList = database.getAreaList().getAreas();
		areaList.remove(area);
		database.getAreaList().setAreas(areaList);
		log.info("Removed Area " + area.getName() + " with distance to Robot " + area.getDistanceToRobot());
	}

	public static void updateAreaShapes() {
		log.info("Update Area Shapes...");
		SickDatabase database = SickDatabase.getInstance();
		double robotPositionX = database.getRobotPositionX();
		double robotPositionY = database.getRobotPositionY();
		List<Area> oldAreaList = database.getAreaList().getAreas();
		ArrayList<Area> newAreaList = new ArrayList<Area>();
		for (Area oldArea : oldAreaList) {
			List<Coordinate> coordinates = SickUtils.calculateCoordinates(robotPositionX, robotPositionY,
					oldArea.getDistanceToRobot());
			Shape newShape = new Shape(oldArea.getShape().getType(), coordinates);
			Area newArea = new Area(oldArea.getId(), oldArea.getName(), oldArea.getLayer(), newShape,
					oldArea.getDistanceToRobot());
			newAreaList.add(newArea);
		}
		AreaList areaList = new AreaList(newAreaList);
		database.setAreaList(areaList);
		log.info("Area Shapes Updated");
	}

	public static boolean updateNearestActiveAreaIN(ActiveBadge badge, ActiveArea activeAreaWithBadge) {
		SickDatabase database = SickDatabase.getInstance();
		ActiveArea nearestActiveArea = database.getNearestActiveArea();
		if (nearestActiveArea == null) {
			database.setNearestActiveArea(activeAreaWithBadge);
			return true;
		} else {
			if (activeAreaWithBadge.getLevel() < nearestActiveArea.getLevel()) {
				nearestActiveArea = activeAreaWithBadge;
				database.setNearestActiveArea(nearestActiveArea);
				return true;
			} else if (activeAreaWithBadge.getLevel() == nearestActiveArea.getLevel()) {
				// True because check for Role
				return true;
			}
		}
		return false;
	}

	public static boolean updateNearestActiveAreaOUT(ActiveBadge badge, ActiveArea activeAreaWithoutBadge) {
		SickDatabase database = SickDatabase.getInstance();
		ActiveArea nearestActiveArea = database.getNearestActiveArea();
		int lastLevel = -1;
		if (nearestActiveArea.getArea().getId().equals(activeAreaWithoutBadge.getArea().getId())) {
			if (nearestActiveArea.getContaingBatchesList().size() == 0) {
				lastLevel = nearestActiveArea.getLevel();
				nearestActiveArea = null;
				ArrayList<ActiveArea> activeAreasList = database.getActiveAreasList();
				Collections.sort(activeAreasList, new ActiveAreaComparator());
				for (ActiveArea currentActiveArea : activeAreasList) {
					if (currentActiveArea.getLevel() > lastLevel
							&& currentActiveArea.getContaingBatchesList().size() > 0) {
						nearestActiveArea = currentActiveArea;
						database.setNearestActiveArea(nearestActiveArea);
						return true;
					}
				}
			}
		}
		// Check for leaving geofence
		if (lastLevel == 3 && nearestActiveArea == null) {
			database.setNearestActiveArea(nearestActiveArea);
			return true;
		}
		return false;
	}

	public static boolean isAreaInDataBase(Long id) {
		SickDatabase database = SickDatabase.getInstance();
		for (Area areaToCheck : database.getAreaList().getAreas()) {
			if (areaToCheck.getId() == id.intValue()) {
				return true;
			}
		}
		return false;
	}

	public static RoleType getLowestRoleInActiveArea(ActiveArea activeArea) {
		RoleType lowestRole = activeArea.getHighestRoleType();
		List<ActiveBadge> containgBatchesList = activeArea.getContaingBatchesList();
		for (ActiveBadge activeBadge : containgBatchesList) {
			if (lowestRole == RoleType.PROFESSOR && (activeBadge.getRole().equals(RoleType.LABORANT)
					|| activeBadge.getRole().equals(RoleType.VISITOR))) {
				lowestRole = activeBadge.getRole();
			} else if (lowestRole == RoleType.LABORANT || activeBadge.getRole().equals(RoleType.VISITOR)) {
				lowestRole = activeBadge.getRole();
			}
		}
		return lowestRole;
	}
}
