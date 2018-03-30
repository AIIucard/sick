
import java.net.URI;
import java.net.URISyntaxException;
import main.htw.handler.RtlsConnectionManager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author b00341
 */
public class PlaygroundMain {
    
    public static void main(String[] args) {
        try {
            // open websocket
            final RtlsConnectionManager clientEndPoint = new RtlsConnectionManager (new URI("unsere"));

            // add listener
            clientEndPoint.addMessageHandler(new RtlsConnectionManager.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });

            // send message to websocket
            clientEndPoint.sendMessage("{'event':'addChannel','channel':'ok_btccny_ticker'}");

            // wait 5 seconds for messages from websocket
            Thread.sleep(5000);

        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
}

