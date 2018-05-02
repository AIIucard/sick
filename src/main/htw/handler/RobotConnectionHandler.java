package main.htw.handler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import org.opcfoundation.ua.core.ApplicationType;
import org.opcfoundation.ua.transport.security.SecurityMode;

import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.SessionActivationException;
import com.prosysopc.ua.client.ConnectException;
import com.prosysopc.ua.client.UaClient;

import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;

public class RobotConnectionHandler extends SickConnectionHandler {

	private static Object lock = new Object();
	private static RobotConnectionHandler instance = null;
	private static UaClient client;

	private static URI uri;

	private RobotConnectionHandler() {
		// Use getInstance
	}

	public static RobotConnectionHandler getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new RobotConnectionHandler();
					try {
						if (propManager == null) {
							propManager = CFGPropertyManager.getInstance();
						}
						uri = new URI(propManager.getProperty(PropertiesKeys.ROBOT_BASE_URL));
					} catch (URISyntaxException e) {
						// TODO: Log
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					setStatusPending();
					initializeConnection();
				}
			}
		}
		return (instance);

	}

	private static void initializeConnection() {
		log.info("Connecting to Robot at" + uri + "...");

		try {
			client = new UaClient(uri.toString());
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
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatusError();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatusError();
		} catch (SessionActivationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatusError();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatusError();
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

}
