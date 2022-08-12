package com.bistel.acovery.dataproc.model;

public class ParameterConfig {
	
	private String id;
	private String name;

	public ParameterConfig(String id, String name) {
		this.id = id;
		
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name + ", " + id;
	}

}
