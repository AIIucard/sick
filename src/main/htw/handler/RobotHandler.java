package main.htw.handler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.core.ApplicationType;
import org.opcfoundation.ua.core.Attributes;
import org.opcfoundation.ua.transport.security.SecurityMode;

import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.client.UaClient;

import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;

public class RobotHandler extends SickHandler {

	private static Object lock = new Object();
	private static RobotHandler instance = null;
	private static UaClient client;

	private static URI uri;

	private RobotHandler() {
		// Use getInstance
	}

	public static RobotHandler getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new RobotHandler();
					try {
						propManager = CFGPropertyManager.getInstance();
						uri = new URI(propManager.getProperty(PropertiesKeys.ROBOT_BASE_URL));
					} catch (URISyntaxException e) {
						// TODO: Log
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return (instance);
	}

	public void initializeConnection() throws Exception {

		log.info("Connecting to Robot at" + uri + "...");
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
	}

	// public void postSecurityLevel() {
	//
	// try {
	// NodeId nodeId = new NodeId(3,
	// "\"GDB_OPC-UA\".\"Security\".\"UserAnnäherung\"");
	// DataValue value = client.readValue(nodeId);
	// log.info("Test:" + value.getValue().toString());
	// } catch (ServiceException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (StatusException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	public void sendSecurityLevel(int securityLevel) {
		try {

			NodeId nodeId = new NodeId(3, "\"GDB_OPC-UA\".\"Security\".\"UserAnnäherung\"");

			if (client.writeAttribute(nodeId, Attributes.Value, new Short(new Integer(securityLevel).toString()))) {
				log.info("Write attribute check" + securityLevel);
			}

		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
