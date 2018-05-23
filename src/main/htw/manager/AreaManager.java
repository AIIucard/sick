package main.htw.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.database.SickDatabase;
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
		try {
			CFGPropertyManager propManager = CFGPropertyManager.getInstance();
			sickPosArea = Integer.parseInt(propManager.getProperty(PropertiesKeys.ZIGPOS_SICK_LAYER));
			return sickPosArea;
		} catch (IOException e) {
			log.error("Cannot determine Layer!");
			e.printStackTrace();
		}

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

	// TODO: Implement, Sort Active Area List by Level
	public static void AddAreaToActiveArea(Area area, int level) {
		log.warn("AddAreaToActiveArea - not implemented!");
		return;
		// SickDatabase db = SickDatabase.getInstance();
		// List<ActiveArea> activeAreas = new ArrayList<>();
		// activeAreas = db.getActiveAreasList();
		//
		// ActiveArea activeArea = new ActiveArea(area, level);
		// sort by distance
	}

	// TODO: Add ActiveBadge to Active Area
	// return ActiveArea

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
}
