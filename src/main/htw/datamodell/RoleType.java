package main.htw.datamodell;

/**
 * All available Role Types which can be assigned to a badge object. The
 * following roles can be used:
 * <ul>
 * <li>Visitor
 * <li>Laborant
 * <li>Professor
 * </ul>
 */
public enum RoleType {

	VISITOR("Visitor"), LABORANT("Laborant"), PROFESSOR("Professor");

	private final String role;

	RoleType(String role) {
		this.role = role;
	}

	/**
	 * Gets the RoleType based on the input string. This method is case sensitive.
	 * If the input string doesn't match "<strong>Visitor</strong>" ,
	 * "<strong>Laborant</strong>" or "<strong>Professor</strong>" the method will
	 * return Null.
	 * 
	 * @param type
	 * @return RoleType
	 */
	public static RoleType getTypeByString(String type) {
		switch (type) {
		case "Visitor":
			return RoleType.VISITOR;

		case "Laborant":
			return RoleType.LABORANT;

		case "Professor":
			return RoleType.PROFESSOR;

		}
		return null;
	}

	@Override
	public String toString() {
		return role;
	}
}
