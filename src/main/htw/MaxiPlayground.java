package main.htw;

import org.slf4j.LoggerFactory;

import main.htw.datamodell.RoleType;
import main.htw.handler.DMNHandler;

public class MaxiPlayground {
	private static org.slf4j.Logger log = LoggerFactory
			.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public static void main(String[] args) {
		DMNHandler dmnHandler = DMNHandler.getInstance();
		dmnHandler.evaluateDecision(RoleType.PROFESSOR.toString(), 1);
	}
}
