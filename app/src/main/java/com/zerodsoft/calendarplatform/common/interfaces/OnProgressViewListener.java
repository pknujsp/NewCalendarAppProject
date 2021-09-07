package com.zerodsoft.calendarplatform.common.interfaces;

public interface OnProgressViewListener {
	void onSuccessfulProcessingData();

	void onFailedProcessingData(String text);

	void onStartedProcessingData(String statusText);

	void onStartedProcessingData();
}
