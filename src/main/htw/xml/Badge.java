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

	public Badge() {
		// Default constructor
	}

	public Badge(String address, RoleType visitor) {
		this.address = address;
		this.role = visitor.toString();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}