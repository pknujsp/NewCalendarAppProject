package com.zerodsoft.scheduleweather.event.foods.enums;

import androidx.room.Ignore;

import com.zerodsoft.scheduleweather.common.enums.LocationIntentCode;

public enum CriteriaLocationType {
	TYPE_SELECTED_LOCATION(0),
	TYPE_MAP_CENTER_POINT(1),
	TYPE_CURRENT_LOCATION_GPS(2),
	TYPE_CUSTOM_SELECTED_LOCATION(3);

	private final int value;

	CriteriaLocationType(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	public static CriteriaLocationType enumOf(int value) throws IllegalArgumentException {
		for (CriteriaLocationType criteriaLocationType : values()) {
			if (value == criteriaLocationType.value) {
				return criteriaLocationType;
			}
		}
		throw new IllegalArgumentException();
	}
}
