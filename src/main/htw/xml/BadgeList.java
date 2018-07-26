package main.htw.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class BadgeList is used to save an AreaList in xml or load/administrate
 * the xml as java object.
 */
@XmlRootElement(name = "badges")
@XmlAccessorType(XmlAccessType.FIELD)
public class BadgeList {

	@XmlElement(name = "badge")
	private List<Badge> badges = new ArrayList<Badge>();

	/**
	 * Empty default constructor required for xml mapping with javax annotations.
	 */
	public BadgeList() {
		// Default constructor
	}

	/**
	 * Instantiates a new badge list.
	 *
	 * @param badges
	 *            the badges
	 */
	public BadgeList(ArrayList<Badge> badges) {
		this.badges = badges;
	}

	/**
	 * Gets the badges.
	 *
	 * @return the badges
	 */
	public List<Badge> getBadges() {
		return badges;
	}

	/**
	 * Sets the badges.
	 *
	 * @param badges
	 *            the new badges
	 */
	public void setBadges(List<Badge> badges) {
		this.badges = badges;
	}

	/**
	 * Adds the badge.
	 *
	 * @param badge
	 *            the badge
	 */
	public void addBadge(Badge badge) {
		if (badges == null) {
			badges = new ArrayList<Badge>();
		}
		this.badges.add(badge);
	}

	/**
	 * Removes the badge.
	 *
	 * @param badge
	 *            the badge
	 */
	public void removeBadge(Badge badge) {
		this.badges.remove(badge);
	}

	/**
	 * Gets the badge by address.
	 *
	 * @param address
	 *            the address
	 * @return the badge by address
	 */
	public Badge getBadgeByAddress(String address) {
		Badge badge = null;
		for (Badge b : this.badges) {
			if (b.getAddress().equalsIgnoreCase(address))
				badge = b;
		}

		return badge;
	}
}