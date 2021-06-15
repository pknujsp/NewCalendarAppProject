package com.zerodsoft.scheduleweather.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressViewListener;

import javax.annotation.Nonnull;

public class CustomProgressView extends LinearLayout implements OnProgressViewListener {
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
		setOrientation(VERTICAL);
		final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());
		setPadding(0, padding, 0, padding);

		progressStatusTextView = new TextView(getContext());
		progressStatusTextView.setGravity(Gravity.CENTER);
		progressStatusTextView.setText(null);
		progressStatusTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
		progressStatusTextView.setId(R.id.progress_status_textview);

		progressView = new CircularProgressIndicator(getContext());
		progressView.setIndeterminate(true);

		LinearLayout.LayoutParams statusTextViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		statusTextViewLayoutParams.gravity = Gravity.CENTER;

		LinearLayout.LayoutParams progressViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		progressViewLayoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f,
				getResources().getDisplayMetrics());
		progressViewLayoutParams.gravity = Gravity.CENTER;

		progressStatusTextView.setLayoutParams(statusTextViewLayoutParams);
		progressView.setLayoutParams(progressViewLayoutParams);

		addView(progressStatusTextView);
		addView(progressView);
	}

	public void setContentView(View contentView) {
		this.contentView = contentView;
	}

	@Override
	public void onSuccessfulProcessingData() {
		progressStatusTextView.setVisibility(View.GONE);
		progressView.setVisibility(View.GONE);
		contentView.setVisibility(View.VISIBLE);
		setVisibility(View.GONE);
	}

	@Override
	public void onFailedProcessingData(@Nonnull String text) {
		progressStatusTextView.setVisibility(View.VISIBLE);
		progressView.setVisibility(View.GONE);
		contentView.setVisibility(View.GONE);
		setVisibility(View.VISIBLE);

		progressStatusTextView.setText(text);
	}

	@Override
	public void onStartedProcessingData(String statusText) {
		progressView.setVisibility(View.VISIBLE);
		progressStatusTextView.setVisibility(View.VISIBLE);
		progressStatusTextView.setText(statusText);
		contentView.setVisibility(View.GONE);
		setVisibility(View.VISIBLE);
	}

	@Override
	public void onStartedProcessingData() {
		progressView.setVisibility(View.VISIBLE);
		progressStatusTextView.setVisibility(View.GONE);
		progressStatusTextView.setText("");
		contentView.setVisibility(View.GONE);
		setVisibility(View.VISIBLE);
	}
}
