/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.htw.datamodell;

import java.util.ArrayList;
import java.util.List;

import main.htw.xml.Area;

public class ActiveArea {

	private Area area;
	private List<ActiveBadge> containgBatchesList = new ArrayList<ActiveBadge>();

	// Sorted by level. 0 Robot to 3 far awav
	private int level;

	public ActiveArea(Area area, int level) {
		this.area = area;
		this.level = level;
	}

	public void addActiveBadge(ActiveBadge activeBadge) {
		containgBatchesList.add(activeBadge);
	}

	public void removeActiveBadge(ActiveBadge activeBadge) {
		containgBatchesList.remove(activeBadge);
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public List<ActiveBadge> getContaingBatchesList() {
		return containgBatchesList;
	}

	public void setContaingBatchesList(List<ActiveBadge> containgBatchesList) {
		this.containgBatchesList = containgBatchesList;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
