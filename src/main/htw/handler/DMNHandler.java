package main.htw.handler;

import java.io.IOException;
import java.io.InputStream;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNHandler {
	private static Object lock = new Object();
	private static DMNHandler instance = null;
	private static DmnEngine dmnEngine = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

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
		// create a default DMN engine
		dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();

		log.info("DMN engine created...");
		// read the DMN XML file as input stream
		InputStream inputStream = null;

		// parse the DMN decision from the input stream
		DmnDecision decision = dmnEngine.parseDecision("decisionKey", inputStream);

		// create the input variables
		VariableMap variables = null;

		// evaluate the decision
		DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);

		// or if the decision is implemented as decision table then you can also use
		result = dmnEngine.evaluateDecisionTable(decision, variables);
	}
}
