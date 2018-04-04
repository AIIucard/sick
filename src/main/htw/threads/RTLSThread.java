package main.htw.threads;

public class RTLSThread implements Runnable {

	private volatile boolean running = true;

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
