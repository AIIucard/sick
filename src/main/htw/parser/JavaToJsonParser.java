package main.htw.parser;

import main.htw.manager.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.xml.Area;
import main.htw.xml.Coordinate;
import main.htw.xml.Shape;

public class JavaToJsonParser {

	public static String getAreaJson(Area area) {
		String jsonString = "";

		jsonString += "{";
		// add id
		// jsonString += "\"id\": " + area.getId() + ",";

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
		// int count = shape.getCoordinates().size() - 1;
		// int tmp = 0;
		for (Coordinate c : shape.getCoordinates()) {
			jsonString += "[ " + c.getX() + ", " + c.getY() + " ],";
			// if (tmp != count) {
			// jsonString += ",";
			// }
			//
			// tmp++;
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
