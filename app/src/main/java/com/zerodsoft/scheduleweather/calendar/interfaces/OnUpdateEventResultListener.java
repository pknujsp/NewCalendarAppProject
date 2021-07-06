package com.zerodsoft.scheduleweather.calendar.interfaces;

import android.os.Parcelable;

import java.io.Serializable;

public interface OnUpdateEventResultListener extends Parcelable {

	void onResultUpdatedAllEvents(long begin);

	void onResultUpdatedThisEvent(long eventId, long begin);

	void onResultUpdatedFollowingEvents(long eventId, long begin);
}