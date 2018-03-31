package main.htw.threads;

public class RobotThread implements Runnable {

	private volatile boolean running = true;

	@Override
	public void run() {
		while (running) {

		}
	}

	public void terminate() {
		running = false;
	}

}
