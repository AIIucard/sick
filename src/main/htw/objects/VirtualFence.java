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
public class VirtualFence {
    
    private Integer id;
    private Integer layer;
    private String label;
    // the area range defines the distance from the light gate to the outer border
    // of the virtual fence so that the coordinates can be calculated
    private Float areaRange; 

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLayer() {
        return layer;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Float getAreaRange() {
        return areaRange;
    }

    public void setAreaRange(Float areaRange) {
        this.areaRange = areaRange;
    }

    
}
