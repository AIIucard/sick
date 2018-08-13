package main.htw.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.SickApplication;
import main.htw.database.SickDatabase;
import main.htw.datamodell.ActiveBadge;
import main.htw.datamodell.RoleType;
import main.htw.xml.Badge;

/**
 * The BadgeManager contains all functions necessary for managing the badges.
 * These include:
 * <ul>
 * <li>Adding/ removing badges to active badges
 * <li>Get a badge by an address
 * <li>Check if the SickDatabase contains a specific badge
 * <li>Add a badge to the database
 * <li>Handle the role edit of a badge
 * <li>Update the name of a badge
 * </ul>
 */
public class BadgeManager {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	/**
	 * Search by address for a specific badge in the {@link SickDatabase}.
	 * 
	 * @param address
	 *            the address of the badge to find.
	 * @return the badge if an badge was found with the specific address;
	 *         <code>null</code> otherwise
	 */
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

	/**
	 * Search by address for a specific active badge in the {@link SickDatabase}.
	 * 
	 * @param address
	 *            the address of the active badge to find.
	 * @return the active badge if an active badge was found with the specific
	 *         address; <code>null</code> otherwise
	 */
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

	/**
	 * Check by address if a badge exists in the {@link SickDatabase}.
	 * 
	 * @param address
	 *            the address from the badge to find
	 * @return <code>true</code> if a badge was found with the specific address;
	 *         <code>false</code> otherwise
	 */
	public static boolean isBadgeInDataBase(String address) {
		SickDatabase database = SickDatabase.getInstance();
		for (Badge badgeToCheck : database.getBadgeList().getBadges()) {
			if (badgeToCheck.getAddress().equals(address)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check by address if an active badge exists in the {@link SickDatabase}.
	 * 
	 * @param address
	 *            the address from the active badge to find
	 * @return <code>true</code> if an active badge was found with the specific
	 *         address; <code>false</code> otherwise
	 */
	public static boolean isActiveBadgeInDataBase(String address) {
		SickDatabase database = SickDatabase.getInstance();
		for (ActiveBadge badgeToCheck : database.getActiveBadgesList()) {
			if (badgeToCheck.getAddress().equals(address)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a none existing badge to the {@link SickDatabase}.
	 * 
	 * @param badgeToAdd
	 *            the badge to add.
	 */
	public static void addBadge(Badge badgeToAdd) {
		if (!isBadgeInDataBase(badgeToAdd.getAddress())) {
			SickDatabase.getInstance().getBadgeList().addBadge(badgeToAdd);
			log.info("Added Badge " + badgeToAdd.getAddress() + " to Database!");
		} else {
			log.error("Database alread contains a badge with the address: " + badgeToAdd.getAddress() + "!");
		}
	}

	/**
	 * Add a none existing badge to the active badges in the {@link SickDatabase}.
	 * 
	 * @param badgeToAdd
	 *            the badge to add.
	 */
	public static void addBadgeToActiveBadges(Badge badgeToAdd) {
		ActiveBadge activeBadge = new ActiveBadge(badgeToAdd);
		if (!BadgeManager.isActiveBadgeInDataBase(activeBadge.getAddress())) {
			SickDatabase.getInstance().getActiveBadgesList().add(activeBadge);
			log.info("Badge " + badgeToAdd.getAddress() + " connected and added to Active Badges!");
		}
	}

	/**
	 * Change the role of an existing badge in the {@link SickDatabase}.
	 * 
	 * @param oldBadge
	 *            the badge with the role to update.
	 * @param badgeRole
	 *            the new badge role.
	 * @return the updated badge with the new role.
	 */
	public static Badge editBadgeRole(Badge oldBadge, String badgeRole) {
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

	/**
	 * Change the name of an existing badge in the {@link SickDatabase}.
	 * 
	 * @param address
	 *            the address of the badge to update.
	 * @param name
	 *            the new name of the badge.
	 */
	public static void updateBadgeName(String address, String name) {
		if (isBadgeInDataBase(address)) {
			if (!BadgeManager.getBadgeByAddress(address).getName().equals(name)) {
				SickDatabase database = SickDatabase.getInstance();
				List<Badge> badgeList = database.getBadgeList().getBadges();

				int pos = 0;

				for (int i = 0; i < badgeList.size(); i++) {
					Badge badgeToCheck = badgeList.get(i);
					if (badgeToCheck.getAddress().equals(address)) {
						pos = i;
						Badge updateBadge = new Badge(address, name, RoleType.getTypeByString(badgeToCheck.getRole()));
						badgeList.set(pos, updateBadge);
						database.getBadgeList().setBadges(badgeList);
						SickApplication.updateBadgeTable(address, updateBadge);
						log.info("Update Badge " + address + " Name: " + name);
					}
				}
			}
		} else {
			log.error("Badge " + address + " is not in Database!");
		}
	}
}
