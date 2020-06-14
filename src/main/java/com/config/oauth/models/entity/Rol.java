package com.config.oauth.models.entity;

import java.io.Serializable;


import org.springframework.stereotype.Component;

@Component
public class Rol implements Serializable{

	private static final long serialVersionUID = -2436202030223033118L;
	private int id;
	private String nombre;
	private String enabled;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
}