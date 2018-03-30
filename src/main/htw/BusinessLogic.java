package main.htw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessLogic implements Runnable {

    private volatile boolean running = true;
    
    private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(100);
                log.trace("Running...");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                log.error(e.getLocalizedMessage());
                e.printStackTrace();
                running = false;
            }
        }
    }

    public void terminate() {
        running = false;
    }
}
