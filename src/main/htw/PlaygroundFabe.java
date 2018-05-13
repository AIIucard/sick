package main.htw;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

import main.htw.database.SickDatabase;
import main.htw.handler.RTLSConnectionHandler;
import main.htw.handler.SickMessageHandler;
import main.htw.properties.CFGPropertyManager;

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

			final RTLSConnectionHandler connectionManager = RTLSConnectionHandler.getInstance();
			try {
				// connectionManager.registerGeoFence();
				// Badge badge = new Badge(1, "8121069331292357553", null);
				// sickDatabase.addToBadgeList(badge);
				// sickMessageHandler.onTextMessage(null, geoFenceEvent);
				// connectionManager.getAllAreas();
				connectionManager.getActiveBadges();
			} catch (Exception e) {
				log.error("Error");
				e.printStackTrace();
			}

			Thread.sleep(10);

		} catch (InterruptedException ex) {
			System.err.println("InterruptedException exception: " + ex.getMessage());
		} catch (IOException ex) {
			Logger.getLogger(PlaygroundFabe.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
