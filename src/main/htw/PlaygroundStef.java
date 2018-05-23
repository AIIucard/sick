package main.htw;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

import main.htw.handler.LightHandler;
import main.htw.handler.SickMessageHandler;
import main.htw.properties.CFGPropertyManager;
import main.htw.utils.SickColor;

public class PlaygroundStef {

	private static org.slf4j.Logger log = LoggerFactory
			.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private static String geoFenceEvent = "{\"topic\":\"GEOFENCING_EVENT\",\"payload\":{\"timestamp\":1523438446430,\"customName\":\"IIoT Test Badge\",\"eventType\":\"IN\",\"message\":\"Device 'IIoT Test Badge' enters area 'Handarbeitsplatz'\",\"areaId\":1,\"address\":\"8121069331292357553\"}}";

	public static void main(String[] args) {
		try {
			// open websocket
			System.out.println("Hi");
			CFGPropertyManager propManager = CFGPropertyManager.getInstance();
			SickMessageHandler sickMessageHandler = SickMessageHandler.getInstance();
			// RobotConnectionHandler robbi = RobotConnectionHandler.getInstance();
			// robbi.sendSecurityLevel(4);
			// DMNHandler dmnHandler = DMNHandler.getInstance();
			// dmnHandler.evaluateDecision("Professor", 1);

			// final RTLSConnectionHandler connectionManager =
			// RTLSConnectionHandler.getInstance();
			// try {
			// // connectionManager.registerGeoFence();
			// // sickMessageHandler.onTextMessage(null, geoFenceEvent);
			// // connectionManager.getAllAreas();
			// } catch (Exception e) {
			// log.error("Error");
			// e.printStackTrace();
			// }
			// final RTLSHandler connectionManager = RTLSHandler.getInstance();
			try {
				// connectionManager.registerGeoFence();
				// sickMessageHandler.onTextMessage(null, geoFenceEvent);
				// connectionManager.getAllAreas();
				log.info("Connecting to Light...");
				LightHandler lightHandler = LightHandler.getInstance();
				lightHandler.initializeConnection();
				for (int i = 0; i <= 3; i++) {
					lightHandler.setLight(SickColor.GREEN.toString());
					Thread.sleep(5000);
					lightHandler.setLight(SickColor.YELLOW.toString());
					Thread.sleep(5000);
					lightHandler.setLight(SickColor.RED.toString());
					Thread.sleep(5000);
				}
				log.info("Connected to Light!");
			} catch (Exception e) {
				log.error("Error");
				e.printStackTrace();
			}

			Thread.sleep(10);

		} catch (InterruptedException ex) {
			System.err.println("InterruptedException exception: " + ex.getMessage());
		} catch (IOException ex) {
			Logger.getLogger(PlaygroundMain.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}