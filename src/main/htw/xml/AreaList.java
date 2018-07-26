package main.htw.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class AreaList is used to save an AreaList in xml or load/administrate
 * the xml as java object.
 */
@XmlRootElement(name = "areas")
@XmlAccessorType(XmlAccessType.FIELD)
public class AreaList {

	@XmlElement(name = "area")
	private List<Area> areas = null;

	/**
	 * Empty default constructor required for xml mapping with javax annotations.
	 */
	public AreaList() {
		// Default constructor
	}

	/**
	 * Instantiates a new area list.
	 *
	 * @param areas
	 *            the areas
	 */
	public AreaList(ArrayList<Area> areas) {
		this.areas = areas;
	}

	/**
	 * Gets the areas.
	 *
	 * @return the areas
	 */
	public List<Area> getAreas() {
		return areas;
	}

	/**
	 * Sets the areas.
	 *
	 * @param areas
	 *            the new areas
	 */
	public void setAreas(List<Area> areas) {
		this.areas = areas;
	}
}
