package main.htw.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class Coordinate is used to save the Coordinates in xml or
 * load/administrate the xml as java object.
 */
@XmlRootElement(name = "coordinate")
@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinate {

	private Double x;
	private Double y;

	/**
	 * Empty default constructor required for xml mapping with javax annotations.
	 */
	public Coordinate() {
		// Default constructor
	}

	/**
	 * Instantiates a new coordinate.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the x.
	 *
	 * @param x
	 *            the new x
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the y.
	 *
	 * @param y
	 *            the new y
	 */
	public void setY(double y) {
		this.y = y;
	}
}
