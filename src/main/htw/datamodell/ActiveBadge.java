/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.htw.datamodell;

import main.htw.xml.Badge;

/**
 * ActiveArea is the representation of a badge we want to use in our
 * application. It contains the following attributes:
 * 
 * <ul>
 * <li>id holds the corresponding badge id
 * <li>address holds the address of the badge
 * <li>role holds the corresponding role of the badge
 * <li>currentGeoFence holds the current Geofence where the badge is located
 * </ul>
 */
public class ActiveBadge {
	private Integer id;
	private String address;
	private RoleType role;
	private Integer currentGeoFence = -1;

	/**
	 * Initializes a new ActiveBadge object.
	 * 
	 * @param badge
	 *            The ZigPos Badge object which is relevant for our application
	 */
	public ActiveBadge(Badge badge) {
		this.address = badge.getAddress();
		this.role = RoleType.getTypeByString(badge.getRole());
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id which has to be set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the current GeoFence
	 */
	public Integer getCurrentGeoFence() {
		return currentGeoFence;
	}

	/**
	 * @param currentGeoFence
	 *            the current GeoFence which has to be set
	 */
	public void setCurrentGeoFence(Integer currentGeoFence) {
		this.currentGeoFence = currentGeoFence;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the current address which has to be set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the current role
	 */
	public RoleType getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role which has to be set
	 */
	public void setRole(RoleType role) {
		this.role = role;
	}
}
