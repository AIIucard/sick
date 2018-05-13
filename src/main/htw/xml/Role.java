package main.htw.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "role")
@XmlAccessorType(XmlAccessType.FIELD)
public class Role {

	private String name;

	public Role() {
		// Default constructor
	}

	public Role(String name) {
		this.name = name;
	}

	String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}