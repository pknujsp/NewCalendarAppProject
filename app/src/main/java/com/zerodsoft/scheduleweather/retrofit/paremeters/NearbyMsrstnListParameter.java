package com.zerodsoft.scheduleweather.retrofit.paremeters;

import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;

import java.util.HashMap;
import java.util.Map;

public class NearbyMsrstnListParameter {
	private String serviceKey = HttpCommunicationClient.FIND_STATION_FOR_AIR_CONDITION_SERVICE_SERVICE_KEY;
	private String returnType = "json";
	private String tmX;
	private String tmY;

	public NearbyMsrstnListParameter() {
	}

	public void setTmX(String tmX) {
		this.tmX = tmX;
	}

	public void setTmY(String tmY) {
		this.tmY = tmY;
	}

	public String getTmX() {
		return tmX;
	}

	public String getTmY() {
		return tmY;
	}

	public Map<String, String> getMap() {
		Map<String, String> map = new HashMap<>();
		map.put("serviceKey", serviceKey);
		map.put("returnType", returnType);
		map.put("tmX", tmX);
		map.put("tmY", tmY);
		map.put("ver", "1.0");
		return map;
	}
}
