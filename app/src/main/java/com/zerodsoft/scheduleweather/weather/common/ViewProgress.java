package com.zerodsoft.scheduleweather.weather.common;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.R;

public class ViewProgress {
	private View dataView;
	private ProgressBar progressBar;
	private TextView errorTextView;
	private View errorView;

	public ViewProgress(View dataView, ProgressBar progressBar, TextView errorTextView, View errorView) {
		this.dataView = dataView;
		this.progressBar = progressBar;
		this.errorTextView = errorTextView;
		this.errorView = errorView;
	}

	public void onCompletedProcessingData(boolean isSuccessful) {
		if (isSuccessful) {
			errorTextView.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
			dataView.setVisibility(View.VISIBLE);
			errorView.setVisibility(View.GONE);
		} else {
			errorTextView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			dataView.setVisibility(View.GONE);
			errorView.setVisibility(View.VISIBLE);
		}
	}

	public void onCompletedProcessingData(boolean isSuccessful, String text) {
		onCompletedProcessingData(true);

		if (!isSuccessful) {
			if (text != null) {
				errorTextView.setText(text);
			} else {
				errorTextView.setText(R.string.error);
			}
		}
	}

	public void onStartedProcessingData() {
		errorTextView.setVisibility(View.GONE);
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

	public TextView getErrorTextView() {
		return errorTextView;
	}

	public View getErrorView() {
		return errorView;
	}
}
