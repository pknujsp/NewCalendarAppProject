package com.zerodsoft.scheduleweather.calendarview.interfaces;

import android.provider.CalendarContract;

import androidx.activity.result.ActivityResult;

import com.zerodsoft.scheduleweather.common.enums.EventIntentCode;

public interface OnEditedEventListener {

	default void processEditingEventResult(ActivityResult activityResult) {
		long eventId = activityResult.getData().getLongExtra(CalendarContract.Events._ID, -1L);
		long instanceId = activityResult.getData().getLongExtra(CalendarContract.Instances._ID, -1L);
		long begin = activityResult.getData().getLongExtra(CalendarContract.Instances.BEGIN, -1L);

		switch (EventIntentCode.enumOf(activityResult.getResultCode())) {
			case RESULT_SAVED:
				onSavedNewEvent(eventId, begin);
				break;
			case RESULT_MODIFIED_EVENT:
				onModifiedEvent(eventId, begin);
				break;
			case RESULT_MODIFIED_THIS_INSTANCE:
				onModifiedInstance(instanceId, begin);
				break;
			case RESULT_MODIFIED_AFTER_INSTANCE_INCLUDING_THIS_INSTANCE:
				break;
		}
	}

	default void onEditingEventResult(ActivityResult activityResult) {
	}

	default void onSavedNewEvent(long eventId, long begin) {
	}

	default void onModifiedEvent(long eventId, long begin) {
	}

	default void onModifiedInstance(long instanceId, long begin) {
	}

	default void moveCurrentView(long begin) {
	}
}
