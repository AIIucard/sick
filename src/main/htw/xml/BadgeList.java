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
	private List<Badge> badges = null;

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
		this.badges.add(badge);
	}

	public void removeBadge(Badge badge) {
		this.badges.remove(badge);
	}
}