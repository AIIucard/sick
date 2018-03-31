package main.htw.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessLogicThread implements Runnable {

	private volatile boolean running = true;
	private static Thread t = null;
	private static RTLSThread logic = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public BusinessLogicThread() {
		logic = new RTLSThread();
		t = new Thread(logic, "RTLSMainThread");
		t.start();
	}

	@Override
	public void run() {
		// Establish Connection to Robot
		// Check Status
		// while (running) {
		// try {
		// Thread.sleep(100);
		//
		// log.trace("Running...");
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// log.error(e.getLocalizedMessage());
		// e.printStackTrace();
		// running = false;
		// }
		// }
	}

	public void terminate() {
		running = false;
		if (t != null && logic != null) {
			logic.terminate();
		}
	}
}
