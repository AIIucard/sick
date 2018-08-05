package main.htw.parser;

import main.htw.manager.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.xml.Area;
import main.htw.xml.Coordinate;
import main.htw.xml.Shape;

/**
 * Java to Json Parser <br>
 * Parses a JSON formatted string from a given Java object. Useful to prepare
 * strings in order to be send to the ZigPos RTLS System. The JavaToJsonParsers
 * supports the following Java objects:
 * <li>Area
 */
public class JavaToJsonParser {

	/**
	 * Parses a JSON formatted string from an given Area object. The resulting
	 * string can be used to interact with the ZigPos RTLS System.
	 * 
	 * @param area
	 *            The area object which will be parsed to a JSON formatted String.
	 * @return JSON formatted string containing a ZigPos readable Area object.
	 */
	public static String getAreaJson(Area area) {
		String jsonString = "";

		jsonString += "{";

		// add inOutHysteresis
		jsonString += "\"inOutHysteresis\": "
				+ CFGPropertyManager.getInstance().getProperty(PropertiesKeys.IN_OUT_HYSTERESIS) + ",";

		// add layer
		jsonString += "\"layer\": " + area.getLayer() + ",";

		// add name
		jsonString += "\"name\": \"" + area.getName() + "\",";

		// add shape
		jsonString += "\"shape\": {";
		Shape shape = area.getShape();

		// add shape.Coordinate
		jsonString += "\"type\": \"" + shape.getType() + "\",";
		jsonString += "\"coordinates\": [ [";
		for (Coordinate c : shape.getCoordinates()) {
			jsonString += "[ " + c.getX() + ", " + c.getY() + " ],";
		}
		Coordinate c = shape.getCoordinates().get(0);
		jsonString += "[" + c.getX() + ", " + c.getY() + "]";

		jsonString += "] ]";

		jsonString += "},";

		// add shapeType
		jsonString += "\"shapeType\": 1";

		// close
		jsonString += "}";

		return jsonString;
	}

}
