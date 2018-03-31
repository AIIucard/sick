/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.htw.messages;

import org.json.JSONArray;

/**
 *
 * @author stefan.riedel
 */
public class MessageDevice {
    
    private long timestamp;
    private boolean connected;
    private int networkid;
    private int anchorID;
    private int shortAddr;
    private int parentAddr;
    private String networkRole;
    private String networkType;
    private String appRole;
    private int deviceState;
    private boolean activated;
    private String customName;
    private String customType;
    private String hardwareName;
    private int softwareversion;
    private float battery;
    private int rssi;
    private String rangingCapabilities;
    private String shortAddrAsHexString;
    private int address;
    private String addressAsHexString;
    private String parentAddrAsHexString;
    private String softwareVersionAsString;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getNetworkid() {
        return networkid;
    }

    public void setNetworkid(int networkid) {
        this.networkid = networkid;
    }

    public int getAnchorID() {
        return anchorID;
    }

    public void setAnchorID(int anchorID) {
        this.anchorID = anchorID;
    }

    public int getShortAddr() {
        return shortAddr;
    }

    public void setShortAddr(int shortAddr) {
        this.shortAddr = shortAddr;
    }

    public int getParentAddr() {
        return parentAddr;
    }

    public void setParentAddr(int parentAddr) {
        this.parentAddr = parentAddr;
    }

    public String getNetworkRole() {
        return networkRole;
    }

    public void setNetworkRole(String networkRole) {
        this.networkRole = networkRole;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getAppRole() {
        return appRole;
    }

    public void setAppRole(String appRole) {
        this.appRole = appRole;
    }

    public int getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(int deviceState) {
        this.deviceState = deviceState;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public String getHardwareName() {
        return hardwareName;
    }

    public void setHardwareName(String hardwareName) {
        this.hardwareName = hardwareName;
    }

    public int getSoftwareversion() {
        return softwareversion;
    }

    public void setSoftwareversion(int softwareversion) {
        this.softwareversion = softwareversion;
    }

    public float getBattery() {
        return battery;
    }

    public void setBattery(float battery) {
        this.battery = battery;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getRangingCapabilities() {
        return rangingCapabilities;
    }

    public void setRangingCapabilities(String rangingCapabilities) {
        this.rangingCapabilities = rangingCapabilities;
    }

    public String getShortAddrAsHexString() {
        return shortAddrAsHexString;
    }

    public void setShortAddrAsHexString(String shortAddrAsHexString) {
        this.shortAddrAsHexString = shortAddrAsHexString;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public String getAddressAsHexString() {
        return addressAsHexString;
    }

    public void setAddressAsHexString(String addressAsHexString) {
        this.addressAsHexString = addressAsHexString;
    }

    public String getParentAddrAsHexString() {
        return parentAddrAsHexString;
    }

    public void setParentAddrAsHexString(String parentAddrAsHexString) {
        this.parentAddrAsHexString = parentAddrAsHexString;
    }

    public String getSoftwareVersionAsString() {
        return softwareVersionAsString;
    }

    public void setSoftwareVersionAsString(String softwareVersionAsString) {
        this.softwareVersionAsString = softwareVersionAsString;
    }
    
}
