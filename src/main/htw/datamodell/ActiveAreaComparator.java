package main.htw.datamodell;

import java.util.Comparator;

public class ActiveAreaComparator implements Comparator<ActiveArea> {

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
