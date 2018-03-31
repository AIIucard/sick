package main.htw.handler;

import java.io.IOException;

public class RuleHandler implements IHandler {

	private static Object lock = new Object();
	private static RuleHandler instance = null;

	public static RuleHandler getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new RuleHandler();
				}
			}
		}
		return (instance);
	}

	private RuleHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleConnection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleReply() {
		// TODO Auto-generated method stub

	}

}
