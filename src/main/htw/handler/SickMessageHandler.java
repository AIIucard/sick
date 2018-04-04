/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.htw.handler;

import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author richter
 */
public class SickMessageHandler {

    private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());
    private static Object lock = new Object();
    private static SickMessageHandler instance = null;

    public static SickMessageHandler getInstance() throws IOException {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new SickMessageHandler();
                }
            }
            log.info("Message handler initialized");
        }
        return (instance);
    }

    public void handleMessage(String message) {
        JSONParser parser = new JSONParser();
        log.info("handle message: " + message);

        try {
            Object obj = parser.parse(message);
            JSONObject jsonObject = (JSONObject) obj;

            String topic = (String) jsonObject.get("topic");
            log.info("topic: " + topic);
            JSONObject payload = (JSONObject) jsonObject.get("payload");
            log.info("payload: " + payload);

            switch (topic) {
                case "GEOFENCING_EVENT":
                    this.handleGeofencingEvent(payload);
                    break;
                default:
                    log.warn("unsupported topic: " + topic);
                    break;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void handleGeofencingEvent(JSONObject payload) {
        // get Area ID 
        // Badge ID
        log.warn("NOT IMPLEMENTED");
    }
}
