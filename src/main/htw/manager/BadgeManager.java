package main.htw.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.database.SickDatabase;
import main.htw.datamodell.ActiveBadge;
import main.htw.xml.Badge;

public class BadgeManager {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

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
