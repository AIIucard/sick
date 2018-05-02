package main.htw.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

import org.opcfoundation.ua.core.ApplicationType;
import org.opcfoundation.ua.transport.security.SecurityMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.SessionActivationException;
import com.prosysopc.ua.client.ConnectException;
import com.prosysopc.ua.client.UaClient;

import main.htw.utils.ConnectionStatusType;

public class RobotHandler {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private static Object lock = new Object();
	private static RobotHandler instance = null;
	private static UaClient client;

	private static ConnectionStatusType connectionStatus;

	private RobotHandler() {
		// Use getInstance
	}

	public static RobotHandler getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new RobotHandler();
					initializeConnection();
				}
			}
		}
		return (instance);

	}

	private static boolean initializeConnection() {
		String uri = "opc.tcp://141.56.181.21:4840/";
		log.info("Connecting to " + uri);

		try {
			client = new UaClient(uri);
			client.setSecurityMode(SecurityMode.NONE);
			org.opcfoundation.ua.core.ApplicationDescription appDescription = new org.opcfoundation.ua.core.ApplicationDescription();
			appDescription.setApplicationName(
					new org.opcfoundation.ua.builtintypes.LocalizedText("SimpleClient", Locale.ENGLISH));
			// 'localhost' (all lower case) in the URI is converted to the actual
			// host name of the computer in which the application is run
			appDescription.setApplicationUri("urn:localhost:UA:SimpleClient");
			appDescription.setProductUri("urn:prosysopc.com:UA:SimpleClient");
			appDescription.setApplicationType(ApplicationType.Client);

			final com.prosysopc.ua.ApplicationIdentity identity = new com.prosysopc.ua.ApplicationIdentity();
			identity.setApplicationDescription(appDescription);
			client.setApplicationIdentity(identity);
			client.connect();

			log.info("Connection succsessful");
			return true;

		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SessionActivationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	// protected static void initialize(UaClient client) throws IOException,
	// UnknownHostException {
	// // *** Application Description is sent to the server
	// org.opcfoundation.ua.core.ApplicationDescription appDescription = new
	// org.opcfoundation.ua.core.ApplicationDescription();
	// appDescription.setApplicationName(new LocalizedText("SimpleClient",
	// Locale.ENGLISH));
	// // 'localhost' (all lower case) in the URI is converted to the actual
	// // host name of the computer in which the application is run
	// appDescription.setApplicationUri("urn:localhost:UA:SimpleClient");
	// appDescription.setProductUri("urn:prosysopc.com:UA:SimpleClient");
	// appDescription.setApplicationType(ApplicationType.Client);
	//
	// final ApplicationIdentity identity = new ApplicationIdentity();
	// identity.setApplicationDescription(appDescription);
	// client.setApplicationIdentity(identity);
	// }

	public static ConnectionStatusType getConnectionStatus() {
		return connectionStatus;
	}
}
