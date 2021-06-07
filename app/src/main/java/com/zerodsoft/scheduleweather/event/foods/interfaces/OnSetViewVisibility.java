package com.zerodsoft.scheduleweather.event.foods.interfaces;

import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OnSetViewVisibility {
	private FragmentContainerView header;
	private FragmentContainerView content;
	private BottomNavigationView bottomNavigationView;

	public OnSetViewVisibility(FragmentContainerView header, FragmentContainerView content, BottomNavigationView bottomNavigationView) {
		this.header = header;
		this.content = content;
		this.bottomNavigationView = bottomNavigationView;
	}

	public enum ViewType {
		HEADER,
		CONTENT,
		BOTTOM_NAV
	}

	public void setVisibility(ViewType viewType, int visibility) {
		switch (viewType) {
			case HEADER:
				header.setVisibility(visibility);
				break;
			case CONTENT:
				content.setVisibility(visibility);
				break;
			case BOTTOM_NAV:
				bottomNavigationView.setVisibility(visibility);
				break;
			default:
				assert (false) : "Unknown";
		}
	}

	public void setHeaderHeight(int heightDP) {
		header.getLayoutParams().height = heightDP;
		header.requestLayout();
		header.invalidate();
	}
}
