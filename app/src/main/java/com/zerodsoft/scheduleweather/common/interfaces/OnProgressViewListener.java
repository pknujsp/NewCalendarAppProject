package com.zerodsoft.scheduleweather.common.interfaces;

import android.view.View;

import com.zerodsoft.scheduleweather.R;

public interface OnProgressViewListener {
	void onSuccessfulProcessingData();

	void onFailedProcessingData(String text);

	void onStartedProcessingData(String statusText);

	void onStartedProcessingData();
}
