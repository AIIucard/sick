package main.htw.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import main.htw.datamodell.RoleType;

// TODO: Auto-generated Javadoc
/**
 * The Class Badge is used to save an Badge in xml or load/administrate the xml
 * as java object.
 */
@XmlRootElement(name = "badge")
@XmlAccessorType(XmlAccessType.FIELD)
public class Badge {

	/** The address. */
	private String address;

	/** The role. */
	private String role;

	/** The name. */
	private String name;

	/**
	 * Empty default constructor required for xml mapping with javax annotations.
	 */
	public Badge() {
		// Default constructor
	}

	/**
	 * Instantiates a new badge.
	 *
	 * @param address
	 *            the address
	 * @param name
	 *            the name
	 * @param visitor
	 *            the visitor
	 */
	public Badge(String address, String name, RoleType visitor) {
		this.address = address;
		this.name = name;
		this.role = visitor.toString();
	}

	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the address.
	 *
	 * @param address
	 *            the new address
	 */
	public void setAddress(String address) {
		this.address = address;
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
	 * Gets the role.
	 *
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Sets the role.
	 *
	 * @param role
	 *            the new role
	 */
	public void setRole(String role) {
		this.role = role;
	}
}