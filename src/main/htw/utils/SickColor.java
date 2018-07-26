package main.htw.utils;

/**
 * The Enum SickColor.
 */
public enum SickColor {

	BLUE("BLUE"), GREEN("GREEN"), YELLOW("YELLOW"), RED("RED"), WHITE("WHITE");

	private final String colorAsString;

	/**
	 * Instantiates a new sick color.
	 *
	 * @param colorAsString
	 *            the color as string
	 */
	SickColor(String colorAsString) {
		this.colorAsString = colorAsString;
	}

	@Override
	public String toString() {
		return colorAsString;
	}
}
