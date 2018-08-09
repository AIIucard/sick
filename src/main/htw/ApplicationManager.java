package main.htw;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.database.SickDatabase;
import main.htw.services.BusinessLogicService;
import main.htw.services.InitializationService;
import main.htw.services.LightConnectionService;
import main.htw.services.RTLSConnectionService;
import main.htw.services.RobotConnectionService;

/**
 * ApplicationManager is a singleton, so the instantiation is restricted to one
 * object. The ApplicationManager coordinates the following actions:
 * <ul>
 * <li>Start/ Stop of the Application
 * <li>Manage and handle all corresponding services including:
 * <ul>
 * <li>the InitializationService
 * <li>the RobotConnectionService
 * <li>the RTLSConnectionService
 * <li>the RTLSConnectionService
 * </ul>
 * <li>Handle the service status
 * <li>Handle the incoming GeofenceEvent
 * </ul>
 */
public class ApplicationManager {

	private boolean isRunning = false;

	private static Object lock = new Object();
	private static ApplicationManager instance = null;

	private static RobotConnectionService robotService = null;
	private static RTLSConnectionService rtlsService = null;
	private static LightConnectionService lightService = null;
	private static InitializationService initializationService = null;

	private static SickDatabase database = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	/**
	 * Use <code>getInstance</code> method!
	 * 
	 * @deprecated
	 */
	private ApplicationManager() {
		// Use getInstance
	}

	/**
	 * Realizes the singleton pattern with synchronized(lock), ensures that just one
	 * class can instantiate this class in one specific moment. If there is already
	 * an instance of this class, the method returns a reference. The class get
	 * instantiated with the database instance.
	 *
	 * @return the new or referenced instance of this class.
	 */
	public static ApplicationManager getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new ApplicationManager();
					database = SickDatabase.getInstance();
				}
			}
		}
		return (instance);
	}

	/**
	 * Start the application by initializing the associated services. The
	 * application waits for the successful connection attempt of each connection
	 * service using an ExecutorService and CountDownLatch. After the successful
	 * connection, the InitializationService is started in which the areas and
	 * badges are updated and the registration is performed in the RTLS system. The
	 * double start of the application is prevented by the flag isRunning.
	 */
	public void startApplication() {
		if (!isRunning) {
			isRunning = true;

			ExecutorService executorService = Executors.newCachedThreadPool();
			CountDownLatch latch = new CountDownLatch(3);

			robotService = new RobotConnectionService(database, latch);
			robotService.setExecutor(executorService);
			robotService.startTheService();

			rtlsService = new RTLSConnectionService(database, latch);
			rtlsService.setExecutor(executorService);
			rtlsService.startTheService();

			lightService = new LightConnectionService(database, latch);
			lightService.setExecutor(executorService);
			lightService.startTheService();
			executorService.shutdown();

			initializationService = new InitializationService(database, latch);
			initializationService.startTheService();
		} else {
			log.debug("Tried to start new thread but application is already running!");
		}
	}

	/**
	 * Stops the application and resets the flag isRunning.
	 */
	public void stopApplication() {
		isRunning = false;
	}

	/**
	 * Returns the running status of the application.
	 * 
	 * @return <code>true</code> if the application is running; <code>false</code>
	 *         otherwise.
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Starts a new BusinessLogigService and forwards the registered GeofenceEvent
	 * to it.
	 * 
	 * @param payload
	 *            the containing payload of the GeofenceEvent as a JSON string
	 */
	public void handleGeofenceEvent(JSONObject payload) {
		if (isRunning) {
			BusinessLogicService businessLogicService = new BusinessLogicService(SickDatabase.getInstance(), payload);
			businessLogicService.start();
		}
	}
}