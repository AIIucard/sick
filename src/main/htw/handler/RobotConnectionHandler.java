package main.htw.handler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;

import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.core.ApplicationType;
import org.opcfoundation.ua.transport.security.SecurityMode;

import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.SessionActivationException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.client.ConnectException;
import com.prosysopc.ua.client.UaClient;
import com.prosysopc.ua.samples.server.MyBigNodeManager.DataItem;

import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;

public class RobotConnectionHandler extends SickConnectionHandler {

	private static Object lock = new Object();
	private static RobotConnectionHandler instance = null;
	private static UaClient client;
	private Map<String, DataItem> dataItems;

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

	public void postSecurityLevel() {

		try {
			NodeId nodeId = new NodeId(3, "\"dbAppPar\".\"Par1\".\"iOgr\"");
			DataValue value = client.readValue(nodeId);
			log.info("Test:" + value.getValue().toString());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendSecurityLevel(int securityLevel) {

		try {
			NodeId nodeId = new NodeId(3, "\"dbAppPar\".\"Par1\".\"iOgr\"");
			// TODO: Int16 Datatype
			client.writeValue(nodeId, new Integer(securityLevel));
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public void sendSecurityLevel(int Securitylevel) {
	// NodeId nodeId = new NodeId(0, 131072);
	// DataValue value = client.readAttribute(nodeId, attributeId);
	// log.info(value.getValue().toString());
	// }
	// boolean status = client.writeAttribute(arg0, arg1, arg2)
	// }
	//
}