package com.bistel.acovery.dataproc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AssetParameter {
	
	private String asset_name;
	private String parameter_name;
	private String sensor_type;
	private String sensor_id;
	
	public ArrayList lists;
	

	public String getAssetId() {
		return asset_name;
	}

	public void setAssetId(String asset_id) {
		this.asset_name = asset_id;
	}

	public String getParameterId() {
		return parameter_name;
	}

	public void setParameterId(String parameter_id) {
		this.parameter_name = parameter_id;
	}
	
	public String getSensorType() {
		return sensor_type;
	}

	public void setSensorType(String sensor_type) {
		this.sensor_type = sensor_type;
	}
	
	public String getSensorId() {
		return sensor_id;
	}

	public void setSensorId(String sensor_id) {
		this.sensor_id = sensor_id;
	}
	
	public String toString() {
		String tempstr = parameter_name  + ", " + asset_name + ", " + sensor_type + ", " + sensor_id ;
//		String[] strlists = tempstr.split(",");
		return tempstr;
	}

	

}
