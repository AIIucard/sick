package main.htw.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "roles")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoleList {

	@XmlElement(name = "role")
	private List<Role> roles = null;

	public RoleList() {
		// Default constructor
	}

	public RoleList(ArrayList<Role> roles) {
		this.roles = roles;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
