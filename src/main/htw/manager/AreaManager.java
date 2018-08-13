package main.htw.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.database.SickDatabase;
import main.htw.datamodell.ActiveArea;
import main.htw.datamodell.ActiveBadge;
import main.htw.datamodell.RoleType;
import main.htw.properties.PropertiesKeys;
import main.htw.utils.ActiveAreaComparator;
import main.htw.utils.SickUtils;
import main.htw.xml.Area;
import main.htw.xml.AreaList;
import main.htw.xml.Coordinate;
import main.htw.xml.Shape;

/**
 * The AreaManager contains all functions necessary for managing the areas.
 * These include:
 * <ul>
 * <li>Adding/ removing active badges to active areas
 * <li>Manage and handle all corresponding services including:
 * <li>Utilty methods for working with areas:
 * <ul>
 * <li>Get area by ID
 * <li>Get active area by ID
 * <li>Check if area exists by ID
 * <li>Check for dublicate areas
 * <li>Update area shapes
 * <li>Check if database contains a specific area
 * </ul>
 * <li>Handle roles for areas
 * <li>Handle the nearest active area
 * </ul>
 */
public class AreaManager {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	/**
	 * This method is for adding an ActiveBadge to an ActiveArea. The badge list of
	 * the area is updated and the highest role in the area is redefined.
	 * 
	 * @param activeBadge
	 *            the active badge to add.
	 * @param activeArea
	 *            the active area to which the badge should be added.
	 * @return the updated active area with the added badge.
	 */
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

	/**
	 * This method is for removing an ActiveBadge from an ActiveArea. The badge list
	 * of the area is updated and the highest role in the area is redefined.
	 * 
	 * @param activeBadge
	 *            the active badge to remove.
	 * @param activeArea
	 *            the active area from which the badge should be removed.
	 * @return the updated active area without the removed badge.
	 */
	public static ActiveArea removeActiveBadgeFromActiveArea(ActiveBadge activeBadge, ActiveArea activeArea) {
		ActiveArea activeAreaWithoutBadge = null;
		SickDatabase database = SickDatabase.getInstance();
		ArrayList<ActiveArea> activeAreasList = database.getActiveAreasList();
		for (ActiveArea currentActiveArea : activeAreasList) {
			if (currentActiveArea.equals(activeArea)) {

				// Count Roles
				int laborantNumber = 0;
				int professorNumber = 0;
				for (ActiveBadge currentActiveBadge : currentActiveArea.getContaingBadgesList()) {

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

	/**
	 * Search for an area in the {@link SickDatabase} by ID
	 * 
	 * @param id
	 *            the ID from the area to find
	 * @return the area if an area was found with the specific id; <code>null</code>
	 *         otherwise
	 */
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

	/**
	 * Search for an active area in the {@link SickDatabase} by ID
	 * 
	 * @param id
	 *            the ID from the active area to find
	 * @return the active area if an active area was found with the specific id;
	 *         <code>null</code> otherwise
	 */
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

	/**
	 * Check by ID if an active area exists in the {@link SickDatabase}
	 * 
	 * @param id
	 *            the ID from the area to find
	 * @return <code>true</code> if an area was found with the specific id;
	 *         <code>false</code> otherwise
	 */
	public static boolean isActiveAreaInDatabase(int id) {
		SickDatabase database = SickDatabase.getInstance();
		List<ActiveArea> activeAreas = database.getActiveAreasList();
		for (ActiveArea activeArea : activeAreas) {
			if (activeArea.getArea().getId().intValue() == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Updating an area in the {@link SickDatabase}. The name and distance to the
	 * robot will be updated. If the distance is changed, the coordinates are also
	 * recalculated.
	 * 
	 * @param oldArea
	 *            the area to update.
	 * @param areaName
	 *            the new name of the area.
	 * @param distanceToRobot
	 *            the new distance to the robot of the area.
	 * @return the updated area with the new name and distance.
	 */
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

				int sickPosArea = getSickAreaLayer();
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

	/**
	 * Check if an area to update already exists with the same name and distance in
	 * the {@link SickDatabase}.
	 * 
	 * @param oldName
	 *            the old name of the area to updated
	 * @param oldDistance
	 *            the old distance of the area to update
	 * @param updatedName
	 *            the updated name of the area to updated
	 * @param updatedDistance
	 *            the updated distance of the area to updated the ID from the area
	 *            to find
	 * @return <code>true</code> if another area with the same name or distance
	 *         already exists; <code>false</code> otherwise
	 */
	public static boolean isDublicatedArea(String oldName, Double oldDistance, String updatedName,
			Double updatedDistance) {
		SickDatabase database = SickDatabase.getInstance();
		ArrayList<Area> areaList = new ArrayList<Area>();
		for (Area area : database.getAreaList().getAreas()) {
			if (!(area.getName().equals(oldName) && area.getDistanceToRobot().equals(oldDistance))) {
				areaList.add(area);
			}
		}

		for (Area area : areaList) {
			if (area.getName().equals(updatedName) || area.getDistanceToRobot().equals(updatedDistance)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Update all area shapes in the {@link SickDatabase}.The calculateCoordinates
	 * method is used of the {@link SickUtils} for calculating the new coordinates
	 * for the area shapes.
	 */
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

	/**
	 * Update the pointer to the nearest active area in the
	 * {@link SickDatabase}.This checks whether there is already a nearestActiveArea
	 * or a next higher active area than the previous nearestActiveArea already
	 * contains a badge. Only than the nearest active area has to be updated.
	 * 
	 * @param activeAreaWithBadge
	 *            the updated active area with the added badge.
	 * @return <code>true</code> if the nearest active area has changed;
	 *         <code>false</code> otherwise
	 */
	public static boolean updateNearestActiveAreaIN(ActiveArea activeAreaWithBadge) {
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

	/**
	 * Update the pointer to the nearest active area in the {@link SickDatabase}.
	 * This checks whether the ID of the updated active area matches that of the
	 * nearest active area and whether the updated active area contains exactly 0
	 * badges. Only than the nearest active area has to be updated.
	 * 
	 * @param activeAreaWithoutBadge
	 *            the updated active area without the badge.
	 * @return <code>true</code> if the nearest active area has changed;
	 *         <code>false</code> otherwise
	 */
	public static boolean updateNearestActiveAreaOUT(ActiveArea activeAreaWithoutBadge) {
		SickDatabase database = SickDatabase.getInstance();
		ActiveArea nearestActiveArea = database.getNearestActiveArea();
		int lastLevel = -1;

		// Check if current ActiveArea is affected
		if (nearestActiveArea.getArea().getId().equals(activeAreaWithoutBadge.getArea().getId())) {

			// If NearestActiveArea has no Badges update NearestActiveArea to higher level
			// with badges
			if (nearestActiveArea.getContaingBadgesList().size() == 0) {
				lastLevel = nearestActiveArea.getLevel();
				nearestActiveArea = null;
				ArrayList<ActiveArea> activeAreasList = database.getActiveAreasList();
				Collections.sort(activeAreasList, new ActiveAreaComparator());
				for (ActiveArea currentActiveArea : activeAreasList) {
					if (currentActiveArea.getLevel() > lastLevel
							&& currentActiveArea.getContaingBadgesList().size() > 0) {
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

	/**
	 * Check by ID if an area exists in the {@link SickDatabase}.
	 * 
	 * @param id
	 *            the ID from the area to find
	 * @return <code>true</code> if an area was found with the specific id;
	 *         <code>false</code> otherwise
	 */
	public static boolean isAreaInDatabase(Long id) {
		SickDatabase database = SickDatabase.getInstance();
		for (Area areaToCheck : database.getAreaList().getAreas()) {
			if (areaToCheck.getId() == id.intValue()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the lowest role of an badge in the active area.
	 * 
	 * @param activeArea
	 *            the active area to get the lowest role from.
	 * @return the lowest role of all badges in the active area.
	 */
	public static RoleType getLowestRoleInActiveArea(ActiveArea activeArea) {
		RoleType lowestRole = activeArea.getHighestRoleType();
		List<ActiveBadge> containgBatchesList = activeArea.getContaingBadgesList();
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

	/**
	 * @return the Sick layer for the areas from the {@link CFGPropertyManager}.
	 */
	private static int getSickAreaLayer() {
		int sickPosArea = -1;
		CFGPropertyManager propManager = CFGPropertyManager.getInstance();
		sickPosArea = Integer.parseInt(propManager.getPropertyValue(PropertiesKeys.AREA_LAYER));
		return sickPosArea;
	}
}
