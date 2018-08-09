package main.htw.utils;

import java.util.Comparator;

import main.htw.datamodell.ActiveArea;

/**
 * This class is used to sort the areas in order of their level attribute.
 */
public class ActiveAreaComparator implements Comparator<ActiveArea> {

	/**
	 * @return the compare value for the active areas
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(ActiveArea a1, ActiveArea a2) {
		if (a1.getLevel() == a2.getLevel()) {
			return 0;
		} else if (a1.getLevel() > a2.getLevel()) {
			return +1;
		} else {
			return -1;
		}
	}
}
