package com.zerodsoft.scheduleweather.common.enums;

public enum EventIntentCode {
	RESULT_SAVED(10), RESULT_DELETED(20), RESULT_MODIFIED_EVENT(30),
	REQUEST_NEW_EVENT(40), REQUEST_MODIFY_EVENT(50), RESULT_MODIFIED_THIS_INSTANCE(60), RESULT_MODIFIED_AFTER_INSTANCE_INCLUDING_THIS_INSTANCE(70), REQUEST_EXCEPT_THIS_INSTANCE(80), REQUEST_SUBSEQUENT_INCLUDING_THIS(90), RESULT_EXCEPTED_INSTANCE(100);

	EventIntentCode(int value) {
		this.value = value;
	}

	private final int value;

	public int value() {
		return value;
	}

	public static EventIntentCode enumOf(int value) throws IllegalArgumentException {
		for (EventIntentCode eventIntentCode : values()) {
			if (value == eventIntentCode.value) {
				return eventIntentCode;
			}
		}
		throw new IllegalArgumentException();
	}
}