package com.zerodsoft.calendarplatform.event.foods.share;

import com.zerodsoft.calendarplatform.event.foods.enums.CriteriaLocationType;

public class CriteriaLocationCloud {
	private static String latitude;
	private static String longitude;
	private static String name;
	private static CriteriaLocationType criteriaLocationType;

	private CriteriaLocationCloud() {
	}

	public static void clear() {
		latitude = null;
		longitude = null;
		name = null;
		criteriaLocationType = null;
	}

	public static boolean isEmpty() {
		return latitude == null;
	}

	public static void setCoordinate(CriteriaLocationType criteriaLocationType, String latitude, String longitude, String name) {
		CriteriaLocationCloud.criteriaLocationType = criteriaLocationType;
		CriteriaLocationCloud.latitude = latitude;
		CriteriaLocationCloud.longitude = longitude;
		CriteriaLocationCloud.name = name;
	}

	public static CriteriaLocationType getCriteriaLocationType() {
		return criteriaLocationType;
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
