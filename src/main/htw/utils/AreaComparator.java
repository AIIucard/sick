package main.htw.utils;

import java.util.Comparator;

import main.htw.xml.Area;

/**
 * This class is used to sort the areas in order of their their distance to
 * robot.
 */
public class AreaComparator implements Comparator<Area> {

	/*
	 * @param a1 Area1 a2 Area2
	 * 
	 * @return 0 if the distance is equal +1 if a1 <a2 , else -1
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
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
