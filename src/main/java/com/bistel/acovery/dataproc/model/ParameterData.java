package com.bistel.acovery.dataproc.model;

public class ParameterData {
	private String time;
	private String name;
	private String value;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String toString() {
		return time + "," + name + ", " + value;
	}

}
