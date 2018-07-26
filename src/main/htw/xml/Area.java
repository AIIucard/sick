package main.htw.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class Area is used to save an Area in xml or load/administrate the xml as
 * java object.
 */
@XmlRootElement(name = "area")
@XmlAccessorType(XmlAccessType.FIELD)
public class Area {

	private Integer id;
	private String name;
	private Integer layer;
	private Shape shape = null;
	private Double distanceToRobot = 0.0;

	// required for xml mapping
	/**
	 * Empty default constructor required for xml mapping with javax annotations.
	 */
	public Area() {
		// Default constructor
	}

	/**
	 * Instantiates a new area.
	 *
	 * @param id
	 *            the id
	 * @param name
	 *            the name
	 * @param layer
	 *            the layer
	 * @param shape
	 *            the shape
	 * @param distanceToRobot
	 *            the distance to robot
	 */
	// edit and update
	public Area(Integer id, String name, Integer layer, Shape shape, Double distanceToRobot) {
		this.id = id;
		this.name = name;
		this.layer = layer;
		this.shape = shape;
		this.distanceToRobot = distanceToRobot;
	}

	/**
	 * Instantiates a new area as a template that is later updated.
	 *
	 * @param id
	 *            the id
	 * @param name
	 *            the name
	 * @param layer
	 *            the layer
	 */
	public Area(Integer id, String name, Integer layer) {
		this.id = id;
		this.name = name;
		this.layer = layer;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the layer.
	 *
	 * @return the layer
	 */
	public Integer getLayer() {
		return layer;
	}

	/**
	 * Sets the layer.
	 *
	 * @param layer
	 *            the new layer
	 */
	public void setLayer(Integer layer) {
		this.layer = layer;
	}

	/**
	 * Gets the shape.
	 *
	 * @return the shape
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * Sets the shape.
	 *
	 * @param shape
	 *            the new shape
	 */
	public void setShape(Shape shape) {
		this.shape = shape;
	}

	/**
	 * Gets the distance to robot.
	 *
	 * @return the distance to robot
	 */
	public Double getDistanceToRobot() {
		return distanceToRobot;
	}

	/**
	 * Sets the distance to robot.
	 *
	 * @param distanceToRobot
	 *            the new distance to robot
	 */
	public void setDistanceToRobot(Double distanceToRobot) {
		this.distanceToRobot = distanceToRobot;
	}
}