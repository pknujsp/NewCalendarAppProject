package com.zerodsoft.scheduleweather.retrofit.paremeters;

import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;

import java.util.HashMap;
import java.util.Map;

public class MsrstnListParameter {
	private String serviceKey = HttpCommunicationClient.FIND_STATION_FOR_AIR_CONDITION_SERVICE_SERVICE_KEY;
	private String returnType = "json";
	private String addr;
	private String stationName;

	public MsrstnListParameter() {
	}

	public Map<String, String> getMap() {
		Map<String, String> map = new HashMap<>();
		map.put("serviceKey", serviceKey);
		map.put("returnType", returnType);
		if (addr != null) {
			map.put("addr", addr);
		}
		if (stationName != null) {
			map.put("stationName", stationName);
		}
		return map;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}


}
