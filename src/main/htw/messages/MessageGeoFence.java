/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.htw.messages;

/**
 *
 * @author stefan.riedel
 */
public class MessageGeoFence {
    
    private String message;
    private int timestamp;
    private String eventType;
    private int areasId;
    private int address;
    private String customName;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getAreasId() {
        return areasId;
    }

    public void setAreasId(int areasId) {
        this.areasId = areasId;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }
    
    
}
