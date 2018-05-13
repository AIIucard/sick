package main.htw.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "badge")
@XmlAccessorType(XmlAccessType.FIELD)
public class Badge {

	private Integer id;
	private String address;
	private Role role;

	public Badge() {
		// Default constructor
	}

	public Badge(Integer id, String address, Role role) {
		this.id = id;
		this.address = address;
		this.role = role;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}