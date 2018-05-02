package main.htw.utils;

public enum ConnectionStatusType {

	ALIVE("Connected"), CONNECTING("Connecting"), DEAD("Not Connected");

	private final String displayName;

	ConnectionStatusType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}