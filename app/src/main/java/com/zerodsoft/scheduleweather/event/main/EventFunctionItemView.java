package com.zerodsoft.scheduleweather.event.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.zerodsoft.scheduleweather.R;

public class EventFunctionItemView extends LinearLayout {
	private TextView titleView;
	private ImageView imgView;

	public EventFunctionItemView(Context context) {
		super(context);
	}

	public EventFunctionItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public EventFunctionItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		String title = null;
		Drawable img = null;

		TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.EventFunctionItemView, 0, 0);
		try {
			title = typedArray.getString(R.styleable.EventFunctionItemView_title);
			img = typedArray.getDrawable(R.styleable.EventFunctionItemView_img);
		} catch (Exception e) {

		}
		typedArray.recycle();

		setClickable(true);
		setOrientation(HORIZONTAL);
		int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36f, getResources().getDisplayMetrics());
		setPadding(padding, padding, padding, padding);
		setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_underline));
		imgView = new ImageView(getContext());
		imgView.setImageDrawable(img);
		imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);

		LinearLayout.LayoutParams imgLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		addView(imgView, imgLayoutParams);

		titleView = new TextView(getContext());
		titleView.setText(title);
		titleView.setTextColor(ContextCompat.getColor(getContext(), R.color.black_medium_emphasis));
		titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
		titleView.setTypeface(null, Typeface.BOLD);

		LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		titleLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		titleLayoutParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics());
		addView(titleView, titleLayoutParams);


	}

	@Override
	public void setOnClickListener(@Nullable OnClickListener l) {
		super.setOnClickListener(l);
	}
}

/*
  <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/textview_underline"
        android:orientation="horizontal"
        android:padding="8dp">

        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:scaleType="fitCenter"
            android:src="@drawable/event_svg" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_vertical"
            android:layout_marginLeft="8dp"
            android:text="@string/instance_info"
            android:textColor="@color/black_medium_emphasis"
            android:textSize="18sp"
            android:textStyle="bold" />


    </LinearLayout>
 */