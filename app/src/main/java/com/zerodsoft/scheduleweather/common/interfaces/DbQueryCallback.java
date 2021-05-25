package com.zerodsoft.scheduleweather.common.interfaces;

public interface DbQueryCallback<T> {
	void onResultSuccessful(T resultDto);

	void onResultNoData();
}
