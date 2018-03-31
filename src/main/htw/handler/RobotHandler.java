package main.htw.handler;

import java.io.IOException;

public class RobotHandler implements IHandler {

	private static Object lock = new Object();
	private static RobotHandler instance = null;

	public static RobotHandler getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new RobotHandler();
				}
			}
		}
		return (instance);
	}

	private RobotHandler() {
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
