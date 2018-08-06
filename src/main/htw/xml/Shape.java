package main.htw.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class Shape is used to save a Shape/Polygon in xml or load/administrate
 * the xml as java object.
 */
@XmlRootElement(name = "shape")
@XmlAccessorType(XmlAccessType.FIELD)
public class Shape {
	private String type;

	@XmlElement(name = "coordinates")
	private List<Coordinate> coordinates = null;

	/**
	 * Empty default constructor required for xml mapping with javax annotations.
	 */
	public Shape() {
		// Default constructor
	}

	/**
	 * Instantiates a new shape.
	 *
	 * @param type
	 *            the type
	 * @param coordinates
	 *            the coordinates
	 */
	public Shape(String type, List<Coordinate> coordinates) {
		this.type = type;
		this.coordinates = coordinates;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type
	 *            the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the coordinates.
	 *
	 * @return the coordinates
	 */
	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	/**
	 * Sets the coordinates.
	 *
	 * @param coordinates
	 *            the new coordinates
	 */
	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}
}
