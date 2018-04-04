package main.htw.handler;

import java.io.IOException;
import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */
@ClientEndpoint
public class RTLSConnectionManager {

    private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

    Session userSession = null;

    private SickMessageHandler sickMessageHandler;

    private static Object lock = new Object();
    private static RTLSConnectionManager instance = null;

    public static RTLSConnectionManager getInstance() throws IOException {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new RTLSConnectionManager();
                }
            }
        }
        return (instance);
    }

    public void connectToURI(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            log.error("Connection error! \n" + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        log.info("opening websocket");
        userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        log.info("closing websocket");
        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a
     * client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        if (sickMessageHandler == null) {
            log.warn("Message handler uninitialized!");
            try {
                sickMessageHandler = SickMessageHandler.getInstance();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sickMessageHandler.handleMessage(message);
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(SickMessageHandler msgHandler) {
        sickMessageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        userSession.getAsyncRemote().sendText(message);
    }
}
