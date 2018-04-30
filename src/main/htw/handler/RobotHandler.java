package main.htw.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prosysopc.ua.client.UaClient;

public class RobotHandler implements IHandler {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private static Object lock = new Object();
	private static RobotHandler instance = null;
	private static UaClient client;
	// private static UaClientListener clientListener = new MyUaClientListener();

	public static RobotHandler getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new RobotHandler();
					// String uri = "opc.tcp://141.56.181.21:4840/";
					//
					// log.info("Connecting to " + uri);
					//
					// client.setListener(clientListener);
					//
					// // Use PKI files to keep track of the trusted and rejected server
					// // certificates...
					// final PkiDirectoryCertificateStore certStore = new
					// PkiDirectoryCertificateStore();
					// final DefaultCertificateValidator validator = new
					// DefaultCertificateValidator(certStore);
					// client.setCertificateValidator(validator);
					// // ...and react to validation results with a custom handler (to prompt
					// // the user what to do, if necessary)
					// // validator.setValidationListener(validationListener);
					//
					// // *** Application Description is sent to the server
					// ApplicationDescription appDescription = new ApplicationDescription();
					// // 'localhost' (all lower case) in the ApplicationName and
					// // ApplicationURI is converted to the actual host name of the computer
					// // in which the application is run
					// appDescription.setApplicationName(new LocalizedText(APP_NAME +
					// "@localhost"));
					// appDescription.setApplicationUri("urn:localhost:OPCUA:" + APP_NAME);
					// appDescription.setProductUri("urn:prosysopc.com:OPCUA:" + APP_NAME);
					// appDescription.setApplicationType(ApplicationType.Client);
					//
					// // *** Certificates
					//
					// File privatePath = new File(certStore.getBaseDir(), "private");
					//
					// // Create self-signed certificates
					// KeyPair issuerCertificate = null;
					//
					// // Enable the following to define a CA certificate which is used to
					// // issue the keys.
					//
					// // issuerCertificate =
					// // ApplicationIdentity.loadOrCreateIssuerCertificate(
					// // "ProsysSampleCA", privatePath, "opcua", 3650, false);
					//
					// int[] keySizes = null;
					// // If you wish to use big certificates (4096 bits), you will need to
					// // define two certificates for your application, since to interoperate
					// // with old applications, you will also need to use a small certificate
					// // (up to 2048 bits).
					//
					// // 4096 bits can only be used with Basic256Sha256 security profile,
					// // which is currently not enabled by default, so we will also not define
					// // this by default.
					//
					// // Use 0 to use the default keySize and default file names as before
					// // (for other values the file names will include the key size).
					// // keySizes = new int[] { 0, 4096 };
					//
					// // *** Application Identity
					// // Define the client application identity, including the security
					// // certificate
					// final ApplicationIdentity identity =
					// ApplicationIdentity.loadOrCreateCertificate(appDescription,
					// "Sample Organisation", /* Private Key Password, optional */"opcua", /* Key
					// File Path */privatePath,
					// /* CA certificate & private key, optional */null,
					// /* Key Sizes for instance certificates to create, optional */keySizes,
					// /* Enable renewing the certificate */true);
					//
					// // Create the HTTPS certificate.
					// // The HTTPS certificate must be created, if you enable HTTPS.
					// String hostName = InetAddress.getLocalHost().getHostName();
					// identity.setHttpsCertificate(ApplicationIdentity.loadOrCreateHttpsCertificate(appDescription,
					// hostName, "opcua",
					// issuerCertificate, privatePath, true));
					//
					// client.setApplicationIdentity(identity);
					//
					// // Define our user locale - the default is Locale.getDefault()
					// client.setLocale(Locale.ENGLISH);
					//
					// // Define the call timeout in milliseconds. Default is null - to
					// // use the value of UaClient.getEndpointConfiguration() which is
					// // 120000 (2 min) by default
					// client.setTimeout(30000);
					//
					// // StatusCheckTimeout is used to detect communication
					// // problems and start automatic reconnection.
					// // These are the default values:
					// client.setStatusCheckTimeout(10000);
					// // client.setAutoReconnect(true);
					//
					// // Listen to server status changes
					// client.addServerStatusListener(serverStatusListener);
					//
					// // Define the security mode
					// // - Default (in UaClient) is BASIC128RSA15_SIGN_ENCRYPT
					// // client.setSecurityMode(SecurityMode.BASIC128RSA15_SIGN_ENCRYPT);
					// // client.setSecurityMode(SecurityMode.BASIC128RSA15_SIGN);
					// // client.setSecurityMode(SecurityMode.NONE);
					//
					// // securityMode is defined from the command line
					// client.setSecurityMode(securityMode);
					//
					// // Define the security policies for HTTPS; ALL is the default
					// client.getHttpsSettings().setHttpsSecurityPolicies(HttpsSecurityPolicy.ALL);
					//
					// // Define a custom certificate validator for the HTTPS certificates
					// client.getHttpsSettings().setCertificateValidator(validator);
					//
					// // If the server supports user authentication, you can set the user
					// // identity.
					// if (userName == null) {
					// // - Default is to use Anonymous authentication, like this:
					// client.setUserIdentity(new UserIdentity());
					// } else {
					// // - Use username/password authentication (note requires security,
					// // above):
					// if (passWord == null) {
					// print("Enter password for user " + userName + ":");
					// passWord = readInput(false);
					// }
					// client.setUserIdentity(new UserIdentity(userName, passWord));
					// }
					// // - Read the user certificate and private key from files:
					// // client.setUserIdentity(new UserIdentity(new java.net.URL(
					// // "my_certificate.der"), new java.net.URL("my_protectedkey.pfx"),
					// // "my_protectedkey_password"));
					//
					// // Session timeout 10 minutes; default is one hour
					// // client.setSessionTimeout(600000);
					//
					// // Set endpoint configuration parameters
					// client.getEndpointConfiguration().setMaxByteStringLength(Integer.MAX_VALUE);
					// client.getEndpointConfiguration().setMaxArrayLength(Integer.MAX_VALUE);
					//
					// // TCP Buffer size parameters - these may help with high traffic
					// // situations.
					// // See http://fasterdata.es.net/host-tuning/background/ for some hints
					// // how to use them
					// // TcpConnection.setReceiveBufferSize(700000);
					// // TcpConnection.setSendBufferSize(700000);
					//
					// try {
					// client = new UaClient(uri);
					// client.cer
					// client.connect();
					// log.info("Connection succsessful");
					//
					// } catch (URISyntaxException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// } catch (InvalidServerEndpointException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// } catch (ConnectException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// } catch (SessionActivationException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// } catch (ServiceException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					//
				}
			}
		}
		return (instance);
	}

	private RobotHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleConnection() {
		// TODO
	}

	@Override
	public void handleRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleReply() {
		// TODO Auto-generated method stub

	}

}
