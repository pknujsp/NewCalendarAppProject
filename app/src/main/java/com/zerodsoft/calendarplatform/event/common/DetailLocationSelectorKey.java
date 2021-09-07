package com.zerodsoft.calendarplatform.event.common;

public enum DetailLocationSelectorKey {
	SELECTED_LOCATION_DTO_IN_EVENT("selectedLocationDtoInEvent"),
	LOCATION_NAME_IN_EVENT("locationNameInEvent"),
	SELECTED_LOCATION_DTO_IN_MAP("selectedLocationDtoInMap");

	private final String value;

	DetailLocationSelectorKey(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static DetailLocationSelectorKey enumOf(String value) throws IllegalArgumentException {
		for (DetailLocationSelectorKey detailLocationSelectorKey : values()) {
			if (value.equals(detailLocationSelectorKey.value)) {
				return detailLocationSelectorKey;
			}
		}
		throw new IllegalArgumentException();
	}
}
