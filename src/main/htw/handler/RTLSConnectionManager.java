/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.htw.handler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


/**
 *
 * @author b00341
 */
@ServerEndpoint("/Location")
public class RTLSConnectionManager {
    private Session session;
    
    @OnOpen
    public void onCreateSession() {
        this.session = session;
         System.out.println("Server has started on 127.0.0.1:80.\r\nWaiting for a connection...");
        //bin ich aktiv
    }
    
    @OnMessage
    public void onTextMessage(String message) {         
            System.out.println("message = " + message);
            if(this.session != null && this.session.isOpen()){
                try {
                    this.session.getBasicRemote().sendText("From RTLS Badge:" + message);
                } catch (IOException ex) {
                    Logger.getLogger(RTLSConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }
    
    
    
}
