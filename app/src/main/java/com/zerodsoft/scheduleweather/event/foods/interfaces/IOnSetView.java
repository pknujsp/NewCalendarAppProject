package com.zerodsoft.scheduleweather.event.foods.interfaces;

public interface IOnSetView {
	enum ViewType {
		HEADER,
		CONTENT,
		BOTTOM_NAV
	}

	void setFragmentContainerVisibility(ViewType viewType, int visibility);

	void setFragmentContainerHeight(int height);
}
