package main.htw.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionRuleResult;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.util.Pair;
import main.htw.utils.SickColor;

/**
 * DMNHandler is a singleton, so that instantiation of the class is restricted
 * to one object. Because there is just one object needed to coordinate the
 * following actions. The class uses the Camunda library to process and
 * integrate the business rule management.
 * 
 * <ul>
 * <li>Initializes the DMNEngine
 * <li>Initializes the DMNdecisionList
 * <li>Load and parse the xml based Business Rule Management
 * <li>Evaluate the decision by the given role and geofence
 * </ul>
 *
 */
public class DMNHandler {
	private static Object lock = new Object();
	private static DMNHandler instance = null;
	private static DmnEngine dmnEngine = null;
	private static List<DmnDecision> decisionList = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private final static String DECISION_MODEL = "cfg" + File.separator + "sick.dmn";

	private DMNHandler() {
		// Use getInstance
	}

	/**
	 * Realizes the singleton pattern with synchronized(lock), ensures that just one
	 * class can instantiate this class in one specific moment. If there is already
	 * an instance of this class, the method returns a reference. With the
	 * instantiation of the class, a DMNEngine that processes the Decision Model is
	 * created.
	 *
	 * @return instance
	 */
	public static DMNHandler getInstance() {
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

	/**
	 * Creates the DMNEngine based on the Camunda library. The Method loads and
	 * parses the decision model out of an xml file.
	 *
	 */
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

	/**
	 * The method looks up the decision table for an rule thats fits to the given
	 * input values role and geofence. After a decision is evaluated some string
	 * operation are carried out to format the result.
	 *
	 * @param role
	 *            String that describes the authorization of the person in the lab
	 * @param geofenceLevel
	 *            int that indicates the area a person is moving in
	 * @return Pair<Integer(security level), SickColor(color)> value range <0-10,
	 *         Blue Green YELLOW RED>
	 */
	public Pair<Integer, SickColor> evaluateDecision(String role, int geofenceLevel) {

		// Create Input Variables
		VariableMap variables = Variables.createVariables().putValue("role", role).putValue("geofence", geofenceLevel);

		// Evaluate Decision
		log.info("Try to find decision...");
		DmnDecisionTableResult decisionTableResultList = null;
		for (int i = 0; i <= decisionList.size(); i++) {
			DmnDecision decision = decisionList.get(i);
			decisionTableResultList = dmnEngine.evaluateDecisionTable(decision, variables);
			int resultSize = decisionTableResultList.size();

			if (resultSize != 0) {
				log.debug("Found Decision at rule=" + i);
				break;
			}
		}

		if (decisionTableResultList != null && decisionTableResultList.size() == 1) {
			DmnDecisionRuleResult dmnDecisionRuleResult = decisionTableResultList.get(0);
			log.info("The result is =" + dmnDecisionRuleResult.values());
			String resultAsString = dmnDecisionRuleResult.values().toString();
			resultAsString = resultAsString.replace("[", "");
			resultAsString = resultAsString.replace("]", "");
			String robotSafetyLevel = resultAsString.split(",")[0];
			String lightColor = resultAsString.split(",")[1];
			switch (lightColor) {
			case "BLUE":
				return new Pair<Integer, SickColor>(new Integer(robotSafetyLevel), SickColor.BLUE);
			case "GREEN":
				return new Pair<Integer, SickColor>(new Integer(robotSafetyLevel), SickColor.GREEN);
			case "YELLOW":
				return new Pair<Integer, SickColor>(new Integer(robotSafetyLevel), SickColor.YELLOW);
			case "RED":
				return new Pair<Integer, SickColor>(new Integer(robotSafetyLevel), SickColor.RED);
			default:
				log.error("No result found!");
				return null;
			}
		} else {
			log.error("No result found!");
			return null;
		}
	}
}
