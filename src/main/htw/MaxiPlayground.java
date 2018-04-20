package main.htw;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import main.htw.handler.DMNHandler;

public class MaxiPlayground {
	private static org.slf4j.Logger log = LoggerFactory
			.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public static void main(String[] args) {
		try {
			DMNHandler dmnHandler = DMNHandler.getInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
