/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.htw.objects;

/**
 *
 * @author b00341
 */
public class Badge {
    private String id;
    private String label;
    private Integer currentGeoFence;
    private Integer accessLevel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getCurrentGeoFence() {
        return currentGeoFence;
    }

    public void setCurrentGeoFence(Integer currentGeoFence) {
        this.currentGeoFence = currentGeoFence;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }
    
    
    
    
}
