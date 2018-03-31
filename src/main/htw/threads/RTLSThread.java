package main.htw.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTLSThread implements Runnable {

	private volatile boolean running = true;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public RTLSThread() {

	}

	@Override
	public void run() {
		while (running) {

		}
	}

	public void terminate() {
		running = false;
	}

}
