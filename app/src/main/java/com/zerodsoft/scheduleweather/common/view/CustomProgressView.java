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
import com.zerodsoft.scheduleweather.common.interfaces.OnProgressViewListener;

public class CustomProgressView extends RelativeLayout implements OnProgressViewListener {
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
		progressViewLayoutParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f,
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

	@Override
	public void onSuccessfulProcessingData() {
		progressStatusTextView.setVisibility(View.GONE);
		progressView.setVisibility(View.GONE);
		if (contentView != null) {
			contentView.setVisibility(View.VISIBLE);
		}
		setVisibility(View.GONE);
	}

	@Override
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

	@Override
	public void onStartedProcessingData(String statusText) {
		progressStatusTextView.setVisibility(View.VISIBLE);

		if (contentView != null) {
			contentView.setVisibility(View.GONE);
		}
		progressView.setVisibility(View.VISIBLE);
		setVisibility(View.VISIBLE);

		if (statusText != null) {
			progressStatusTextView.setText(statusText);
		} else {
			progressStatusTextView.setText("");
		}
	}

}
