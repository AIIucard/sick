package main.htw.handler;

import java.io.IOException;

public class LightHandler implements IHandler {

	private static Object lock = new Object();
	private static LightHandler instance = null;

	public static LightHandler getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new LightHandler();
				}
			}
		}
		return (instance);
	}

	private LightHandler() {
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
