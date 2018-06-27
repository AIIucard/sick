package main.htw.xml;

import java.util.Comparator;

public class AreaComparator implements Comparator<Area> {

	@Override
	public int compare(Area a1, Area a2) {
		if (a1.getDistanceToRobot() == a2.getDistanceToRobot()) {
			return 0;
		} else if (a1.getDistanceToRobot() > a2.getDistanceToRobot()) {
			return +1;
		} else {
			return -1;
		}
	}
}
