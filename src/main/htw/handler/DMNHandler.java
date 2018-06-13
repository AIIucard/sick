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

	public Pair<Integer, SickColor> evaluateDecision(String role, int geofenceLevel) {

		// Create Input Variables
		VariableMap variables = Variables.createVariables().putValue("role", role).putValue("geofence", geofenceLevel);

		int decisionTableSize = decisionList.size();

		log.info("Try to find decision...");
		log.debug("Tablesize=" + decisionTableSize + "Entry:" + decisionList.toString());

		// Evaluate Decision
		DmnDecisionTableResult decisionTableResult = null;
		//
		for (int i = 0; i <= decisionTableSize; i++) {
			DmnDecision decision = decisionList.get(i);
			decisionTableResult = dmnEngine.evaluateDecisionTable(decision, variables);
			int resultSize = decisionTableResult.size();

			if (resultSize != 0) {
				log.debug("Found Decision at rule=" + i);
				break;
			}
		}

		if (decisionTableResult != null) {
			DmnDecisionRuleResult dmnDecisionRuleResult = decisionTableResult.get(0);
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
