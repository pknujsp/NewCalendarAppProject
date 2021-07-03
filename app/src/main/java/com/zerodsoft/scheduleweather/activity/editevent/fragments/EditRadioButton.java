package com.zerodsoft.scheduleweather.activity.editevent.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.zerodsoft.scheduleweather.R;

import org.jetbrains.annotations.NotNull;

public class EditRadioButton extends LinearLayout {
	private MaterialRadioButton radioButton;
	private EditText innerEditText;
	private TextView innerTextView;
	private TextView radioTextView;

	private String mainText = "";
	private String subText = "";
	private int innerViewType = 0;

	//edittext또는 클릭 가능한 텍스트뷰 추가가능
	public EditRadioButton(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public EditRadioButton(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.EditRadioButton, 0, 0);
		try {
			innerViewType = a.getInt(R.styleable.EditRadioButton_inner_view_type, innerViewType);
		} finally {
			a.recycle();
		}

		setOrientation(HORIZONTAL);
		setClickable(true);
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				radioButton.setChecked(!radioButton.isChecked());
			}
		});

		TypedValue backgroundTypedValue = new TypedValue();
		getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, backgroundTypedValue, true);
		setBackgroundResource(backgroundTypedValue.resourceId);

		setGravity(Gravity.CENTER_VERTICAL);
		final int viewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, getResources().getDisplayMetrics());
		setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewHeight));

		final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());

		radioButton = new MaterialRadioButton(getContext());
		LinearLayout.LayoutParams radioLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		radioLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		radioLayoutParams.rightMargin = margin;
		addView(radioButton, radioLayoutParams);

		radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				processAfterChecked();
			}
		});

		if (innerViewType == 0) {
			innerEditText = new EditText(getContext());
			innerEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
			innerEditText.setMinEms(3);
			innerEditText.setMaxEms(3);

			LinearLayout.LayoutParams innerEditTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			innerEditTextLayoutParams.gravity = Gravity.CENTER_VERTICAL;
			innerEditTextLayoutParams.rightMargin = margin;
			addView(innerEditText, innerEditTextLayoutParams);
		} else if (innerViewType == 1) {
			innerTextView = new TextView(getContext());
			innerTextView.setBackgroundResource(backgroundTypedValue.resourceId);
			innerTextView.setClickable(true);

			LinearLayout.LayoutParams innerTextViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			innerTextViewLayoutParams.gravity = Gravity.CENTER_VERTICAL;
			innerTextViewLayoutParams.rightMargin = margin;
			addView(innerTextView, innerTextViewLayoutParams);
		}

		radioTextView = new TextView(getContext());
		radioTextView.setClickable(true);
		radioTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				radioButton.setChecked(!radioButton.isChecked());
			}
		});

		LinearLayout.LayoutParams radioTextViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		radioTextViewLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		addView(radioTextView, radioTextViewLayoutParams);
	}

	public MaterialRadioButton getRadioButton() {
		return radioButton;
	}

	public void setInnerTextViewOnClickListener(View.OnClickListener onClickListener) {
		innerTextView.setOnClickListener(onClickListener);
	}

	public void setMainText(String mainText) {
		this.mainText = mainText;
	}

	public void applyMainText() {
		radioTextView.setText(mainText);
	}

	public void setSubText(String subText) {
		this.subText = subText;
	}

	public void applySubText() {
		radioTextView.setText(subText);
	}


	public EditText getInnerEditText() {
		return innerEditText;
	}

	public TextView getInnerTextView() {
		return innerTextView;
	}

	public void setTextOfInnerTextView(String text) {
		innerTextView.setText(text);
	}

	public void setTextOfInnerEditText(String text) {
		innerEditText.setText(text);
	}

	public boolean isChecked() {
		return radioButton.isChecked();
	}

	private void processAfterChecked() {
		if (radioButton.isChecked()) {
			if (innerEditText != null) {
				innerEditText.setEnabled(true);
			}
			if (innerTextView != null) {
				innerTextView.setClickable(true);
			}
		} else {
			if (innerEditText != null) {
				innerEditText.setEnabled(false);
			}
			if (innerTextView != null) {
				innerTextView.setClickable(false);
			}
		}

	}
}
