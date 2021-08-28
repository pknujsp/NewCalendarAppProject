package com.zerodsoft.scheduleweather.event.common.interfaces;

public interface IFab {
	public static final int TYPE_MAIN = 0;
	public static final int TYPE_REMOVE_EVENT = 1;
	public static final int TYPE_MODIFY_EVENT = 2;
	public static final int TYPE_SELECT_LOCATION = 3;

	void setAllVisibility(int visibility);

	void setVisibility(int type, int visibility);

	int getVisibility(int type);
}
