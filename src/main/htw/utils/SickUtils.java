package main.htw.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.database.SickDatabase;
import main.htw.xml.Area;
import main.htw.xml.AreaList;
import main.htw.xml.Coordinate;
import main.htw.xml.Shape;

public class SickUtils {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public static void updateAreaShapes() {
		log.info("Update Area Shapes...");
		SickDatabase database = SickDatabase.getInstance();
		double robotPositionX = database.getRobotPositionX();
		double robotPositionY = database.getRobotPositionY();
		List<Area> oldAreaList = database.getAreaList().getAreas();
		ArrayList<Area> newAreaList = new ArrayList<Area>();
		for (Area oldArea : oldAreaList) {
			List<Coordinate> coordinates = calculateCoordinates(robotPositionX, robotPositionY,
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

	public static List<Coordinate> calculateCoordinates(double robotPositionX, double robotPositionY,
			double distanceToRobot) {

		// 1. left border: (x-distance/2,y)
		// 2. right border: (x+distance/2,y)

		// Coordinates of the corners:

		// 1. north west corner: (x-distance/2,y) + (0,distance/2) =
		// (x-distance/2,y+distance/2)
		// [adding distance/2 to y coordinate of centre point of left border]
		double x1 = robotPositionX - distanceToRobot / 2;
		double y1 = robotPositionY + distanceToRobot / 2;
		Coordinate coodinate1 = new Coordinate(x1, y1);

		// 2. north east corner: (x+distance/2,y) + (0,distance/2) =
		// (x+distance/2,y+distance/2)
		// [adding distance/2 to y coordinate of centre point of right border]
		double x2 = robotPositionX + distanceToRobot / 2;
		double y2 = robotPositionY + distanceToRobot / 2;
		Coordinate coodinate2 = new Coordinate(x2, y2);

		// 3. south west corner: (x-distance/2,y) + (0,-distance/2) =
		// (x-distance/2,y-distance/2)
		// [subtracting s/2 from y coordinate of centre point of left border]
		double x3 = robotPositionX - distanceToRobot / 2;
		double y3 = robotPositionY - distanceToRobot / 2;
		Coordinate coodinate3 = new Coordinate(x3, y3);

		// 4. south east corner: (x+distance/2,y) + (0,-distance/2) =
		// (x+distance/2,y-distance/2)
		// [subtracting distance/2 from y coordinate of centre point of right border]
		double x4 = robotPositionX + distanceToRobot / 2;
		double y4 = robotPositionY - distanceToRobot / 2;
		Coordinate coodinate4 = new Coordinate(x4, y4);

		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		coordinates.add(coodinate1);
		coordinates.add(coodinate2);
		coordinates.add(coodinate3);
		coordinates.add(coodinate4);
		return coordinates;
	}

	public static Area addNewArea(String areaName, Double distanceToRobot) {
		log.info("Create new Area...");
		SickDatabase database = SickDatabase.getInstance();
		double robotPositionX = database.getRobotPositionX();
		double robotPositionY = database.getRobotPositionY();
		List<Area> areaList = database.getAreaList().getAreas();

		List<Coordinate> coordinates = calculateCoordinates(robotPositionX, robotPositionY, distanceToRobot);
		Shape newShape = new Shape("Polygon", coordinates);
		Area newArea = new Area(getNextId(), areaName, 1337, newShape, distanceToRobot);
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

		List<Coordinate> coordinates = calculateCoordinates(robotPositionX, robotPositionY, distanceToRobot);
		Shape newShape = new Shape("Polygon", coordinates);

		int pos = 0;

		for (int i = 0; i < areaList.size(); i++) {
			Area areaToCheck = areaList.get(i);
			if (areaToCheck.getName().equals(areaName) && areaToCheck.getDistanceToRobot().equals(distanceToRobot)) {
				pos = i;
				Area updatedArea = new Area(areaList.get(pos).getId(), areaName, 1337, newShape, distanceToRobot);
				areaList.set(pos, updatedArea);
				database.getAreaList().setAreas(areaList);
				log.info("Updated Area " + areaName + " with distance to Robot " + distanceToRobot);
				return updatedArea;
			}
		}
		return oldArea;
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

	public static void removeArea(Area area) {
		log.info("Remove Area...");
		SickDatabase database = SickDatabase.getInstance();
		List<Area> areaList = database.getAreaList().getAreas();
		areaList.remove(area);
		database.getAreaList().setAreas(areaList);
		log.info("Removed Area " + area.getName() + " with distance to Robot " + area.getDistanceToRobot());
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
}
