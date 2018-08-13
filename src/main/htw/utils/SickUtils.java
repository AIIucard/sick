package main.htw.utils;

import java.util.ArrayList;
import java.util.List;

import main.htw.xml.Area;
import main.htw.xml.Coordinate;

/**
 * Provides utility methods for the application.
 *
 */
public class SickUtils {

	/**
	 * Calculate the shape coordinates based on the robot position and the distance
	 * to the robot.
	 * 
	 * @param robotPositionX
	 *            the X coordinate of the robot position.
	 * @param robotPositionY
	 *            the Y coordinate of the robot position.
	 * @param distanceToRobot
	 *            the distance to the robot.
	 * @return a list of the calculated coordinates for the area shape.
	 */
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

	/**
	 * Check if two different areas have different coordinates.
	 * 
	 * @param areaToCheck
	 *            the first area to check.
	 * @param areaInLayer
	 *            the second area to check.
	 * @return <code>true</code> if areas have different coordinates;
	 *         <code>false</code> otherwise
	 */
	public static boolean hasDifferentCoordinates(Area areaToCheck, Area areaInLayer) {
		List<Coordinate> coordinates1 = areaToCheck.getShape().getCoordinates();
		List<Coordinate> coordinates2 = areaInLayer.getShape().getCoordinates();
		for (int i = 0; i < coordinates1.size(); i++) {
			if ((coordinates1.get(i).getX() != coordinates2.get(i).getX())
					|| (coordinates1.get(i).getY() != coordinates2.get(i).getY()))
				return true;
		}
		return false;
	}
}
