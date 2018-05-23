package main.htw.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.database.SickDatabase;
import main.htw.datamodell.ActiveBadge;
import main.htw.xml.Badge;
import main.htw.xml.Coordinate;

public class SickUtils {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

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

	public static Badge getBadgeByAddress(String address) {
		SickDatabase database = SickDatabase.getInstance();
		List<Badge> badges = database.getBadgeList().getBadges();
		for (Badge badge : badges) {
			if (badge.getAddress().equals(address)) {
				return badge;
			}
		}
		log.error("No such badge registered in Database!");
		return null;
	}

	public static ActiveBadge getActiveBadgeByAddress(String address) {
		SickDatabase database = SickDatabase.getInstance();
		List<ActiveBadge> badges = database.getActiveBadgesList();
		for (ActiveBadge badge : badges) {
			if (badge.getAddress().equals(address)) {
				return badge;
			}
		}
		log.warn("No such active badge registered in Database!");
		return null;
	}

	public static boolean isBadgeInDataBase(String address) {
		SickDatabase database = SickDatabase.getInstance();
		for (Badge badgeToCheck : database.getBadgeList().getBadges()) {
			if (badgeToCheck.getAddress().equals(address)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isActiveBadgeInDataBase(String address) {
		SickDatabase database = SickDatabase.getInstance();
		for (ActiveBadge badgeToCheck : database.getActiveBadgesList()) {
			if (badgeToCheck.getAddress().equals(address)) {
				return true;
			}
		}
		return false;
	}
}
