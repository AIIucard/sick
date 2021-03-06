package main.htw.utils;

/**
 * Contains all connection status types available in the application for a
 * better overview.
 */
public enum ConnectionStatusType {

	OK("Connected"), PENDING("Connecting"), NEW("Not Connected"), ERROR("Connection failed");

	private final String displayName;

	ConnectionStatusType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}