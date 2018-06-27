package main.htw.datamodell;

public enum RoleType {

	VISITOR("Visitor"), LABORANT("Laborant"), PROFESSOR("Professor");

	private final String role;

	RoleType(String role) {
		this.role = role;
	}

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
