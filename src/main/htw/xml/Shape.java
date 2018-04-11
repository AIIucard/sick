package main.htw.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "shape")
@XmlAccessorType(XmlAccessType.FIELD)
public class Shape {
	private String type;

	@XmlElement(name = "coordinates")
	private List<Coordinate> coordinates = null;

	public Shape() {
		// Default constructor
	}

	public Shape(String type, List<Coordinate> coordinates) {
		this.type = type;
		this.coordinates = coordinates;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}
}
