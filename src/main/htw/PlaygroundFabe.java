package main.htw;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

import main.htw.database.SickDatabase;
import main.htw.datamodell.ActiveArea;
import main.htw.handler.RTLSHandler;
import main.htw.handler.SickMessageHandler;
import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.utils.SickUtils;
import main.htw.xml.Area;
import main.htw.xml.Coordinate;
import main.htw.xml.Shape;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author b00341
 */
public class PlaygroundFabe {

	private static org.slf4j.Logger log = LoggerFactory
			.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private static String geoFenceEvent = "{\"topic\":\"GEOFENCING_EVENT\",\"payload\":{\"timestamp\":1523438446430,\"customName\":\"IIoT Test Badge\",\"eventType\":\"IN\",\"message\":\"Device 'IIoT Test Badge' enters area 'Handarbeitsplatz'\",\"areaId\":1,\"address\":\"8121069331292357553\"}}";

	public static void main(String[] args) {
		try {
			// open websocket
			System.out.println("Hi");
			CFGPropertyManager propManager = CFGPropertyManager.getInstance();
			SickMessageHandler sickMessageHandler = SickMessageHandler.getInstance();
			SickDatabase sickDatabase = SickDatabase.getInstance();
			// RobotConnectionHandler robbi = RobotConnectionHandler.getInstance();
			// robbi.sendSecurityLevel(4);

			final RTLSHandler connectionManager = RTLSHandler.getInstance();
			connectionManager.initializeConnection();
			try {
				// connectionManager.registerGeoFence();
				// Badge badge = new Badge(1, "8121069331292357553", null);
				// sickDatabase.addToBadgeList(badge);
				// sickMessageHandler.onTextMessage(null, geoFenceEvent);
				List<Area> areas = connectionManager.getAllAreas();
				for (Area a : areas) {
					log.info("AREA");
					log.info("id  : " + a.getId());
					log.info("name: " + a.getName());
				}

				Shape shape = new Shape();
				shape.setType("Polygon");

				double robotPositionX = Double.parseDouble(propManager.getProperty(PropertiesKeys.ROBOT_X_COORDINATE));
				double robotPositionY = Double.parseDouble(propManager.getProperty(PropertiesKeys.ROBOT_Y_COORDINATE));
				List<Coordinate> coordinates = SickUtils.calculateCoordinates(robotPositionX, robotPositionY, 2);
				shape.setCoordinates(coordinates);

				Area testArea = new Area(5, "Sick Test Area", 2, shape, (double) 1);
				// TODO: send Area to zigpos
				// connectionManager.addArea(testArea);

				List<ActiveArea> aa = connectionManager.getActiveAreasFromZigpos();
				for (ActiveArea a : aa) {
					log.info("ActiveArea Level: " + a.getLevel());
				}

				// connectionManager.getActiveBadges();
			} catch (Exception e) {
				log.error("Error");
				e.printStackTrace();
			}

			Thread.sleep(10);

		} catch (InterruptedException ex) {
			System.err.println("InterruptedException exception: " + ex.getMessage());
		} catch (IOException ex) {
			Logger.getLogger(PlaygroundFabe.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
