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

import main.htw.database.SickDatabase;
import main.htw.manager.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.utils.ConnectionStatusType;

/**
 * RobotHandler is a singleton, so that instantiation of the class is restricted
 * to one object. Because there is just one object needed to coordinate the
 * following actions.
 * 
 * <ul>
 * <li>Initializes the Connection to the Robot
 * <li>Send the calculated security level to the robot via OPCUA
 * </ul>
 *
 */
public class RobotHandler extends SickHandler {

	private static Object lock = new Object();
	private static RobotHandler instance = null;
	private static UaClient client;

	private static URI uri;

	private RobotHandler() {
		// Use getInstance
	}

	/**
	 * Realizes the singleton pattern with synchronized(lock), ensures that just one
	 * class can instantiate this class in one specific moment. If there is already
	 * an instance of this class, the method returns a reference. The class get
	 * instantiated with a URI which is defined and loaded from config file.
	 *
	 * @return instance
	 */
	public static RobotHandler getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new RobotHandler();
					try {
						propManager = CFGPropertyManager.getInstance();
						uri = new URI(propManager.getProperty(PropertiesKeys.ROBOT_BASE_URL));
					} catch (URISyntaxException e) {
						log.error("Can not create new URI for RobotHandler! Got the following Exception: "
								+ e.getLocalizedMessage());
					}
				}
			}
		}
		return (instance);
	}

	/**
	 * Initializes the connection to the loaded URI of the robot instance. The
	 * method ignores certificates through SecurityMode.None. Describes the
	 * connection with an required appDescription and identity. If the connection
	 * attempt is successful, sets database connection flag OK.
	 * 
	 * @throws Exception
	 *             Exception is generalized and depends on the connection type.
	 *             Handle <code>LocalizedMessage()</code> and try to reconnect!
	 */
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

		SickDatabase database = SickDatabase.getInstance();
		database.setRobotConnectionStatus(ConnectionStatusType.OK);
		log.info("Connection successful");
	}

	/**
	 * Sends the security level to the robot. The NodeID is an OPCUA reference which
	 * specifies the value to be written by a layer and the attribute path that can
	 * be red out with UA Expert for a particular system. SecurityLevel is sent when
	 * no exception is thrown. Otherwise look up log.
	 * 
	 * @param securityLevel
	 *            range of values 0-10 -> 0 robot turns off 10 runs at normal speed
	 */
	public void sendSecurityLevel(int securityLevel) {
		try {

			NodeId nodeId = new NodeId(3, "\"GDB_OPC-UA\".\"Security\".\"UserAnnaeherung\"");

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
