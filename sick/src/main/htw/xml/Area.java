package main.htw.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "area")
@XmlAccessorType(XmlAccessType.FIELD)
public class Area {

	private Integer id;
	private String name;
	private Integer layer;
	private Shape shape = null;
	private Double distanceToRobot = 0.0;

	public Area() {
		// Default constructor
	}

	public Area(Integer id, String name, Integer layer, Shape shape, Double distanceToRobot) {
		this.id = id;
		this.name = name;
		this.layer = layer;
		this.shape = shape;
		this.distanceToRobot = distanceToRobot;
	}

	public Area(Integer id, String name, Integer layer) {
		this.id = id;
		this.name = name;
		this.layer = layer;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLayer() {
		return layer;
	}

	public void setLayer(Integer layer) {
		this.layer = layer;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public Double getDistanceToRobot() {
		return distanceToRobot;
	}

	public void setDistanceToRobot(Double distanceToRobot) {
		this.distanceToRobot = distanceToRobot;
	}
}