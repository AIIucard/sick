package main.htw;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

import com.neovisionaries.ws.client.WebSocketException;

import main.htw.handler.RTLSConnectionManager;
import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author b00341
 */
public class PlaygroundMain {

	private static org.slf4j.Logger log = LoggerFactory
			.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public static void main(String[] args) {
		try {
			// open websocket
			System.out.println("Hi");
			CFGPropertyManager propManager = CFGPropertyManager.getInstance();
			URI uri = new URI(propManager.getProperty(PropertiesKeys.ZIGPOS_BASE_URL));

			final RTLSConnectionManager connectionManager = RTLSConnectionManager.getInstance(uri);
			// final SickMessageHandler sickMessageHandler =
			// SickMessageHandler.getInstance();
			log.info("Connecting to " + uri);
			connectionManager.createWebsocket(uri);

			// add listener
			// clientEndPoint.addMessageHandler(SickMessageHandler.getInstance());

			// send message to websocket
			// sickMessageHandler.sendMessage(
			// "{\n" + "\"topic\":\"REGISTER\",\n" +
			// "\"payload\":[\"POSITION\",\"DISTANCES\"]\n" + "}");
			try {
				connectionManager.registerGeoFence();
			} catch (WebSocketException e) {
				log.error("Connection Error");
				e.printStackTrace();
			}

			// // send message to websocket
			// clientEndPoint.sendMessage("{\n" +
			// "\"topic\":\"UNREGISTER\",\n" +
			// "\"payload\":[\"POSITION\",\"DISTANCES\"]\n" +
			// "}");
			// wait 5 seconds for messages from websocket
			Thread.sleep(10);

		} catch (InterruptedException ex) {
			System.err.println("InterruptedException exception: " + ex.getMessage());
		} catch (URISyntaxException ex) {
			System.err.println("URISyntaxException exception: " + ex.getMessage());
		} catch (IOException ex) {
			Logger.getLogger(PlaygroundMain.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}