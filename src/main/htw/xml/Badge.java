package main.htw.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import main.htw.datamodell.RoleType;

@XmlRootElement(name = "badge")
@XmlAccessorType(XmlAccessType.FIELD)
public class Badge {

	private String address;
	private String role;
	private String name;

	public Badge() {
		// Default constructor
	}

	public Badge(String address, String name, RoleType visitor) {
		this.address = address;
		this.name = name;
		this.role = visitor.toString();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}