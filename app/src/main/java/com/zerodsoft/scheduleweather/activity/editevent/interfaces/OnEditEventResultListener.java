package com.zerodsoft.scheduleweather.activity.editevent.interfaces;

import android.os.Parcelable;

import java.io.Serializable;

public interface OnEditEventResultListener extends Parcelable {

	void onSavedNewEvent(long dtStart);

	void onUpdatedOnlyThisEvent(long dtStart);

	void onUpdatedFollowingEvents(long dtStart);

	void onUpdatedAllEvents(long dtStart);

	void onRemovedAllEvents();

	void onRemovedFollowingEvents();

	void onRemovedOnlyThisEvents();
}
