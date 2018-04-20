package main.htw.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.messages.MessageGeoFence;

public class DMNHandler {
	private static Object lock = new Object();
	private static DMNHandler instance = null;
	private static DmnEngine dmnEngine = null;
	private static List<DmnDecision> decisionList = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private final static String DECISION_MODEL = "cfg" + File.separator + "sick.dmn";

	public static DMNHandler getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new DMNHandler();
					createDMNEngine();
				}
			}
		}
		return (instance);
	}

	private DMNHandler() {
		// TODO Auto-generated constructor stub
	}

	private static void createDMNEngine() {

		log.info("Creating a default DMN engine...");
		dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();
		log.info("DMN engine created!");

		log.info("Loading DMN Model...");
		try {
			InputStream inputStream = new FileInputStream(new File(DECISION_MODEL));
			log.info("DMN Model loaded.");
			log.info("Parsing decision model...");
			decisionList = dmnEngine.parseDecisions(inputStream);
			log.info("Decision model parsed!");
		} catch (FileNotFoundException e) {
			log.error("Cannot load user decision model!\n" + e.getLocalizedMessage());
		}
	}

	public void handleGeofenceIn(MessageGeoFence geoFence) {
		// TODO handle IN event
		log.warn("NOT IMPLEMENTED");
	}

	public void handleGeofenceOut(MessageGeoFence geoFence) {
		// TODO handle OUT event
		log.warn("NOT IMPLEMENTED");
	}
}
