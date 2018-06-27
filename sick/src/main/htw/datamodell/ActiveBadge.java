/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.htw.datamodell;

import main.htw.xml.Badge;

/**
 *
 * @author richter
 */
public class ActiveBadge {
	private Integer id;
	private String address;
	private RoleType role;
	private Integer currentGeoFence = -1;

	public ActiveBadge(Badge badge) {
		this.address = badge.getAddress();
		this.role = RoleType.getTypeByString(badge.getRole());
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCurrentGeoFence() {
		return currentGeoFence;
	}

	public void setCurrentGeoFence(Integer currentGeoFence) {
		this.currentGeoFence = currentGeoFence;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public RoleType getRole() {
		return role;
	}

	public void setRole(RoleType role) {
		this.role = role;
	}
}
