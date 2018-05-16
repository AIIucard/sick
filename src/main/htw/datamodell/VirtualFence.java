/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.htw.datamodell;

/**
 *
 * @author b00341
 */
public class VirtualFence {

	// TODO: Check if this is needed (Area XML Object in xml package)

	private Long id;
	private Long layer;
	private String name;
	// the area range defines the distance from the light gate to the outer border
	// of the virtual fence so that the coordinates can be calculated
	private Float areaRange;

	public VirtualFence(Long id, Long layer, String name) {
		this.id = id;
		this.layer = layer;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLayer() {
		return layer;
	}

	public void setLayer(Long layer) {
		this.layer = layer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getAreaRange() {
		return areaRange;
	}

	public void setAreaRange(Float areaRange) {
		this.areaRange = areaRange;
	}

}
