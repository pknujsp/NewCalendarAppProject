package com.zerodsoft.scheduleweather.common.enums;

public enum LocationIntentCode {
	REQUEST_CODE_SELECT_LOCATION_EMPTY_QUERY(100),
	REQUEST_CODE_SELECT_LOCATION_BY_QUERY(200),
	REQUEST_CODE_CHANGE_LOCATION(300),
	RESULT_CODE_CHANGED_LOCATION(400),
	RESULT_CODE_REMOVED_LOCATION(500),
	RESULT_CODE_SELECTED_LOCATION(600);

	private final int value;

	LocationIntentCode(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	public static LocationIntentCode enumOf(int value) throws IllegalArgumentException {
		for (LocationIntentCode locationIntentCode : values()) {
			if (value == locationIntentCode.value) {
				return locationIntentCode;
			}
		}
		throw new IllegalArgumentException();
	}
}
