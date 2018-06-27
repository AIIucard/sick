package main.htw.handler;

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
						uri = new URI("opc.tcp\\://192.168.100.10\\:4840/");
					} catch (URISyntaxException e) {
						log.error("Can not create new URI for RobotHandler! Got the following Exception: "
								+ e.getLocalizedMessage());
					}
				}
			}
		}
		return (instance);
	}

	public void initializeConnection() throws Exception {

		// If connection broke change SickApplication to SimpleClient again
		log.info("Connecting to Robot at" + uri + "...");
		client = new UaClient(uri.toString());
		client.setSecurityMode(SecurityMode.NONE);
		org.opcfoundation.ua.core.ApplicationDescription appDescription = new org.opcfoundation.ua.core.ApplicationDescription();
		appDescription.setApplicationName(
				new org.opcfoundation.ua.builtintypes.LocalizedText("SickApplication", Locale.ENGLISH));
		// 'localhost' (all lower case) in the URI is converted to the actual
		// host name of the computer in which the application is run
		appDescription.setApplicationUri("urn:localhost:UA:SickApplication");
		appDescription.setProductUri("urn:prosysopc.com:UA:SickApplication");
		appDescription.setApplicationType(ApplicationType.Client);

		final com.prosysopc.ua.ApplicationIdentity identity = new com.prosysopc.ua.ApplicationIdentity();
		identity.setApplicationDescription(appDescription);
		client.setApplicationIdentity(identity);
		client.connect();

		// TODO Fix this for Connection
		// TODO Set connected Status in S!CK Main Application
		// SickDatabase database = SickDatabase.getInstance();
		// database.setRobotConnectionStatus(ConnectionStatusType.OK);
		log.info("Connection successful");
	}

	public void sendSecurityLevel(int securityLevel) {
		try {

			NodeId nodeId = new NodeId(3, "\"GDB_OPC-UA\".\"Security\".\"UserAnnäherung\"");

			if (client.writeAttribute(nodeId, Attributes.Value, new Short(new Integer(securityLevel).toString()))) {
				log.info("Write attribute check" + securityLevel);
			}

		} catch (ServiceException e) {
			log.error("Can not send Security Level to OPC-UA Interface! Got the following Exception: "
					+ e.getLocalizedMessage());
		} catch (StatusException e) {
			log.error("Can not send Security Level to OPC-UA Interface! Got the following Exception: "
					+ e.getLocalizedMessage());
		}
	}
}
