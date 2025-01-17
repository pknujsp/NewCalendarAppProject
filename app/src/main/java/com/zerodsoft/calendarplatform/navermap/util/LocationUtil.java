package com.zerodsoft.calendarplatform.navermap.util;

public class LocationUtil {
	private LocationUtil() {
	}

	public static boolean isRestaurant(String placeCategoryStr) {
		return placeCategoryStr.contains("음식점");
	}

	public static String convertMeterToKm(String distance) {
		double meterDistance = Double.parseDouble(distance);

		if (meterDistance >= 1000) {
			return meterDistance / 1000 + "km";
		} else {
			return distance + "m";
		}

	}
}
