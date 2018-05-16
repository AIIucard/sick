package main.htw.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "badges")
@XmlAccessorType(XmlAccessType.FIELD)
public class BadgeList {

	@XmlElement(name = "badge")
	private List<Badge> badges = new ArrayList<Badge>();

	public BadgeList() {
		// Default constructor
	}

	public BadgeList(ArrayList<Badge> badges) {
		this.badges = badges;
	}

	public List<Badge> getBadges() {
		return badges;
	}

	public void setBadges(List<Badge> badges) {
		this.badges = badges;
	}

	public void addBadge(Badge badge) {
		if (badges == null) {
			badges = new ArrayList<Badge>();
		}
		this.badges.add(badge);
	}

	public void removeBadge(Badge badge) {
		this.badges.remove(badge);
	}

	public Badge getBadgeByAddress(String address) {
		Badge badge = null;
		for (Badge b : this.badges) {
			if (b.getAddress().equalsIgnoreCase(address))
				badge = b;
		}

		return badge;
	}
}