package com.zerodsoft.calendarplatform.weather.common;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zerodsoft.calendarplatform.R;

public class ViewProgress {
	private View dataView;
	private ProgressBar progressBar;
	private TextView progressStatusTextview;
	private View errorView;

	public ViewProgress(View dataView, ProgressBar progressBar, TextView progressStatusTextview, View errorView) {
		this.dataView = dataView;
		this.progressBar = progressBar;
		this.progressStatusTextview = progressStatusTextview;
		this.errorView = errorView;
	}

	public void onCompletedProcessingData(boolean isSuccessful) {
		if (isSuccessful) {
			progressStatusTextview.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
			dataView.setVisibility(View.VISIBLE);
			errorView.setVisibility(View.GONE);
		} else {
			progressStatusTextview.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			dataView.setVisibility(View.GONE);
			errorView.setVisibility(View.VISIBLE);
		}
	}

	public void onCompletedProcessingData(boolean isSuccessful, String text) {
		onCompletedProcessingData(isSuccessful);

		if (!isSuccessful) {
			if (text != null) {
				progressStatusTextview.setText(text);
			} else {
				progressStatusTextview.setText(R.string.error);
			}
		}
	}

	public void onStartedProcessingData() {
		progressStatusTextview.setVisibility(View.GONE);
		dataView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		errorView.setVisibility(View.VISIBLE);
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public View getDataView() {
		return dataView;
	}

	public TextView getprogressStatusTextview() {
		return progressStatusTextview;
	}

	public View getErrorView() {
		return errorView;
	}
}
