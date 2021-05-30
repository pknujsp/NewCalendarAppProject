package com.zerodsoft.scheduleweather.common.interfaces;

public abstract class OnPopBackStackFragmentCallback {
	public abstract void onPopped();

	public void pop() {
		onPopped();
	}
}
