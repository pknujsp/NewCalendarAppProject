package com.zerodsoft.scheduleweather.event.foods.share;

import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class CriteriaLocationCloud {
	private static String latitude;
	private static String longitude;

	private CriteriaLocationCloud() {
	}

	public static void setCoordinate(String latitude, String longitude) {
		CriteriaLocationCloud.latitude = latitude;
		CriteriaLocationCloud.longitude = longitude;
	}


	public static String getLatitude() {
		return latitude;
	}

	public static String getLongitude() {
		return longitude;
	}
}
