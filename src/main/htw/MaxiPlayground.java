package main.htw;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.slf4j.LoggerFactory;

import main.htw.xml.Area;
import main.htw.xml.AreaList;
import main.htw.xml.XMLMarshler;

public class MaxiPlayground {
	private static org.slf4j.Logger log = LoggerFactory
			.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public static void main(String[] args) {
		// Coordinate coordinate = new Coordinate(6.138, 11.004);
		// ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
		// coordinates.add(coordinate);
		// Shape shape = new Shape("Polygon", coordinates);
		// Area area = new Area(1, "Room A", 1337, shape);
		// ArrayList<Area> areas = new ArrayList<Area>();
		// areas.add(area);
		// AreaList areaList = new AreaList(areas);

		try {
			XMLMarshler xmlMarshler = XMLMarshler.getInstance();
			AreaList areaList = xmlMarshler.unMarshalAreaList();
			for (Area area : areaList.getAreas()) {
				System.out.println(area.getId());
				System.out.println(area.getName());
				System.out.println(area.getLayer());
			}
			log.info(areaList.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
