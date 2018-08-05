/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.htw.datamodell;

import java.util.ArrayList;
import java.util.List;

import main.htw.xml.Area;

/**
 * ActiveArea is the representation of an area we want to use in our
 * application.
 * 
 *
 */
/**
 * @author richter
 *
 */
/**
 * @author richter
 *
 */
public class ActiveArea {

	private Area area;
	private List<ActiveBadge> containgBadgesList = new ArrayList<ActiveBadge>();

	// Sorted by level. 0 Robot to 3 far awav
	private int level;

	private RoleType highestRoleType = RoleType.VISITOR;

	/**
	 * Constructor for the ActiveArea.
	 * 
	 * @param area
	 *            The ZigPos area object which is relevant for our application.
	 * @param level
	 *            The position of the ActiveArea in the Area Stack. Each area has a
	 *            certain level depending on the distance of its borders to the
	 *            robot cell. The closer the border (and therefore smaller the area)
	 *            the lower the level.
	 */
	public ActiveArea(Area area, int level) {
		this.area = area;
		this.level = level;
	}

	/**
	 * Adds an ActiveBadge to the ActiveArea. This is necessary to know which badge
	 * is currently in which area present.
	 * 
	 * @param activeBadge
	 */
	public void addActiveBadge(ActiveBadge activeBadge) {
		containgBadgesList.add(activeBadge);
	}

	/**
	 * Removes an ActiveBadge from the ActiveArea. This is necessary to know which
	 * badge is currently in which area present.
	 * 
	 * @param activeBadge
	 */
	public void removeActiveBadge(ActiveBadge activeBadge) {
		containgBadgesList.remove(activeBadge);
	}

	/**
	 * Gets the corresponding ZigPos area object belonging to the ActiveArea object.
	 * 
	 * @return area
	 */
	public Area getArea() {
		return area;
	}

	/**
	 * Sets the corresponding ZigPos area object belonging to the ActiveArea object.
	 * 
	 * @param area
	 *            The ZigPos area object which is relevant for our application.
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	/**
	 * Gets all ActiveBadge objects currently present in this ActiveArea.
	 * 
	 * @return containing badges list
	 */
	public List<ActiveBadge> getContaingBadgesList() {
		return containgBadgesList;
	}

	/**
	 * Sets all ActiveBadge objects currently present in this ActiveArea.
	 * 
	 * @param containgBadgesList
	 */
	public void setContaingBadgesList(List<ActiveBadge> containgBadgesList) {
		this.containgBadgesList = containgBadgesList;
	}

	/**
	 * Gets the Level of the ActiveArea in the Area Stack. Each area has a certain
	 * level depending on the distance of its borders to the robot cell. The closer
	 * the border (and therefore smaller the area) the lower the level.
	 * 
	 * @return level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets the Level of the ActiveArea in the Area Stack. Each area has a certain
	 * level depending on the distance of its borders to the robot cell. The closer
	 * the border (and therefore smaller the area) the lower the level.
	 * 
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Gets the highest Role of all badges currently present in this ActiveArea.
	 * Each badge has a specific Role attached to it. It is used to control the
	 * speed of the Robot in conjunction with the nearest distance of a badge to the
	 * robot cell.
	 * 
	 * @return highestRoleType
	 */
	public RoleType getHighestRoleType() {
		return highestRoleType;
	}

	/**
	 * Sets the highest Role of all badges currently present in this ActiveArea.
	 * Each badge has a specific Role attached to it. It is used to control the
	 * speed of the Robot in conjunction with the nearest distance of a badge to the
	 * robot cell.
	 * 
	 * @param highestRoleType
	 */
	public void setHighestRoleType(RoleType highestRoleType) {
		this.highestRoleType = highestRoleType;
	}
}
