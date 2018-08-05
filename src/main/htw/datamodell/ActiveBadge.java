/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.htw.datamodell;

import main.htw.xml.Badge;

/**
 * ActiveBadge is the representation of a badge we want to use in our
 * application. Every badge has a assigned
 * <li>Identifier
 * <li>address
 * <li>role
 * <li>geofence
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
	 * @return id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return currentGeoFence
	 */
	public Integer getCurrentGeoFence() {
		return currentGeoFence;
	}

	/**
	 * @param currentGeoFence
	 */
	public void setCurrentGeoFence(Integer currentGeoFence) {
		this.currentGeoFence = currentGeoFence;
	}

	/**
	 * @return address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return role
	 */
	public RoleType getRole() {
		return role;
	}

	/**
	 * @param role
	 */
	public void setRole(RoleType role) {
		this.role = role;
	}
}
