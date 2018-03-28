package main.htw;

public class BusinessLogic implements Runnable {

	private volatile boolean running = true;

	@Override
	public void run() {
		while (running) {
			try {
				Thread.sleep(100);
				System.out.println("Running...");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				running = false;
			}
		}
	}

	public void terminate() {
		running = false;
	}
}
