package main.htw.utils;

public enum SickColor {

	BLUE("BLUE"), GREEN("GREEN"), YELLOW("YELLOW"), RED("RED");

	private final String colorAsString;

	SickColor(String colorAsString) {
		this.colorAsString = colorAsString;
	}

	@Override
	public String toString() {
		return colorAsString;
	}
}