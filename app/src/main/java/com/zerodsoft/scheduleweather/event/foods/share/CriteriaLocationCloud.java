package com.zerodsoft.scheduleweather.event.foods.share;

public class CriteriaLocationCloud {
	private static String latitude;
	private static String longitude;
	private static String name;

	private CriteriaLocationCloud() {
	}

	public static void setCoordinate(String latitude, String longitude, String name) {
		CriteriaLocationCloud.latitude = latitude;
		CriteriaLocationCloud.longitude = longitude;
		CriteriaLocationCloud.name = name;
	}


	public static String getLatitude() {
		return latitude;
	}

	public static String getLongitude() {
		return longitude;
	}

	public static String getName() {
		return name;
	}
}
