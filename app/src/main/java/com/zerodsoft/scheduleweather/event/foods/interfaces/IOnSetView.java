package com.zerodsoft.scheduleweather.event.foods.interfaces;

public interface IOnSetView {
	enum ViewType {
		HEADER,
		CONTENT,
		BOTTOM_NAV
	}

	void setVisibility(ViewType viewType, int visibility);

	void setHeaderHeight(int heightDP);
}
