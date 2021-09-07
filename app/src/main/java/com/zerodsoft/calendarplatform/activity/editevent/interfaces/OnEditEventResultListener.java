package com.zerodsoft.calendarplatform.activity.editevent.interfaces;

import android.os.Parcelable;

public interface OnEditEventResultListener extends Parcelable {

	void onSavedNewEvent(long dtStart);

	void onUpdatedOnlyThisEvent(long dtStart);

	void onUpdatedFollowingEvents(long dtStart);

	void onUpdatedAllEvents(long dtStart);

	void onRemovedAllEvents();

	void onRemovedFollowingEvents();

	void onRemovedOnlyThisEvents();
}
