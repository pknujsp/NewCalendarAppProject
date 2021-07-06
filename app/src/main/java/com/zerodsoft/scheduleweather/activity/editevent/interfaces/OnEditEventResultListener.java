package com.zerodsoft.scheduleweather.activity.editevent.interfaces;

public interface OnEditEventResultListener {
	void onSavedNewEvent(long dtStart);

	void onUpdatedOnlyThisEvent();

	void onUpdatedFollowingEvents();

	void onUpdatedAllEvents();

	void onRemovedAllEvents();

	void onRemovedFollowingEvents();

	void onRemovedOnlyThisEvents();
}
