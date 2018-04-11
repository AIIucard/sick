package main.htw.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "areas")
@XmlAccessorType(XmlAccessType.FIELD)
public class AreaList {

	@XmlElement(name = "area")
	private List<Area> areas = null;

	public AreaList() {
		// Default constructor
	}

	public AreaList(ArrayList<Area> areas) {
		this.areas = areas;
	}

	public List<Area> getAreas() {
		return areas;
	}

	public void setAreas(List<Area> areas) {
		this.areas = areas;
	}
}
