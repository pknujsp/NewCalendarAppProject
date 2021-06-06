package com.zerodsoft.scheduleweather.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.zerodsoft.scheduleweather.R;

public class CustomProgressView extends RelativeLayout {
	private TextView progressStatusTextView;
	private CircularProgressIndicator progressView;
	private View contentView;

	public CustomProgressView(Context context) {
		super(context);
		init();
	}

	public CustomProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public CustomProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init() {
		progressStatusTextView = new TextView(getContext());
		progressStatusTextView.setGravity(Gravity.CENTER);
		progressStatusTextView.setText(null);
		progressStatusTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
		progressStatusTextView.setId(R.id.progress_status_textview);

		progressView = new CircularProgressIndicator(getContext());
		progressView.setIndeterminate(true);

		RelativeLayout.LayoutParams statusTextViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		statusTextViewLayoutParams.addRule(CENTER_IN_PARENT, TRUE);

		RelativeLayout.LayoutParams progressViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		progressViewLayoutParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f,
				getResources().getDisplayMetrics());
		progressViewLayoutParams.addRule(LEFT_OF, progressStatusTextView.getId());

		progressStatusTextView.setLayoutParams(statusTextViewLayoutParams);
		progressView.setLayoutParams(progressViewLayoutParams);

		addView(progressStatusTextView);
		addView(progressView);
	}

	public void setContentView(View contentView) {
		this.contentView = contentView;
	}

	public void onSuccessfulProcessingData() {
		progressStatusTextView.setVisibility(View.GONE);
		progressView.setVisibility(View.GONE);
		if (contentView != null) {
			contentView.setVisibility(View.VISIBLE);
		}
		setVisibility(View.GONE);
	}

	public void onFailedProcessingData(String text) {
		progressStatusTextView.setVisibility(View.VISIBLE);
		progressView.setVisibility(View.GONE);
		if (contentView != null) {
			contentView.setVisibility(View.GONE);
		}
		setVisibility(View.VISIBLE);

		if (text != null) {
			progressStatusTextView.setText(text);
		} else {
			progressStatusTextView.setText(R.string.error);
		}
	}

	public void onStartedProcessingData() {
		progressStatusTextView.setVisibility(View.GONE);
		if (contentView != null) {
			contentView.setVisibility(View.GONE);
		}
		progressView.setVisibility(View.VISIBLE);
		setVisibility(View.VISIBLE);
	}

	/*
	   <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingTop="12dp"
        android:paddingBottom="12dp">

        <TextView
            android:id="@+id/progress_status_textview"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerInParent="true"
            android:gravity="center"
            android:text="진행 상태"
            android:textSize="13sp" />

        <com.google.android.material.progressindicator.CircularProgressIndicator
            android:id="@+id/progress_bar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginRight="4dp"
            android:layout_toLeftOf="@id/progress_status_textview"
            android:indeterminate="true" />

    </RelativeLayout>
	 */

	/*
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
	 */
}
