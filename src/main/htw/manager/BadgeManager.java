package main.htw.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.database.SickDatabase;
import main.htw.datamodell.ActiveBadge;
import main.htw.datamodell.RoleType;
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

	public static void addBadge(Badge badgeToAdd) {
		SickDatabase.getInstance().getBadgeList().addBadge(badgeToAdd);
		log.info("Added Badge " + badgeToAdd.getAddress() + " to Database!");
	}

	public static void addBadgeToActiveBadges(Badge badgeToAdd) {
		ActiveBadge activeBadge = new ActiveBadge(badgeToAdd);
		if (!BadgeManager.isActiveBadgeInDataBase(activeBadge.getAddress())) {
			SickDatabase.getInstance().getActiveBadgesList().add(activeBadge);
			log.info("Badge " + badgeToAdd.getAddress() + " connected and added to Active Badges!");
		}
	}

	public static Badge editBadge(Badge oldBadge, String badgeRole) {
		log.info("Edit Badge...");
		SickDatabase database = SickDatabase.getInstance();
		List<Badge> badgeList = database.getBadgeList().getBadges();

		int pos = 0;

		for (int i = 0; i < badgeList.size(); i++) {
			Badge badgeToCheck = badgeList.get(i);
			if (badgeToCheck.getAddress().equals(oldBadge.getAddress())) {
				pos = i;

				Badge updateBadge = new Badge(oldBadge.getAddress(), oldBadge.getName(),
						RoleType.getTypeByString(badgeRole));
				badgeList.set(pos, updateBadge);
				database.getBadgeList().setBadges(badgeList);
				log.info("Updated role for Badge " + oldBadge.getAddress() + " to " + badgeRole);
				return updateBadge;
			}
		}
		return oldBadge;
	}
}
