package com.zerodsoft.scheduleweather.common.enums;

public enum CalendarViewType {
	DAY(10), WEEK(20), MONTH(30);

	CalendarViewType(int value) {
		this.value = value;
	}

	private final int value;

	public int value() {
		return value;
	}

	public static CalendarViewType enumOf(int value) throws IllegalArgumentException {
		for (CalendarViewType calendarViewType : values()) {
			if (value == calendarViewType.value) {
				return calendarViewType;
			}
		}
		throw new IllegalArgumentException();
	}
}
