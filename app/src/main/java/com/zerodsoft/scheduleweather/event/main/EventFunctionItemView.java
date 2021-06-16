package com.zerodsoft.scheduleweather.event.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;
import com.zerodsoft.scheduleweather.R;

public class EventFunctionItemView extends MaterialCardView {
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
		setRadius(getResources().getDimension(R.dimen.corner_radius));
		setCardElevation((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, getResources().getDisplayMetrics()));
		setUseCompatPadding(true);

		LinearLayout linearLayout = new LinearLayout(getContext());
		linearLayout.setOrientation(LinearLayout.VERTICAL);

		addView(linearLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		titleView = new TextView(getContext());
		final int titleTextViewPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
		titleView.setPadding(titleTextViewPadding, titleTextViewPadding, titleTextViewPadding, titleTextViewPadding);
		titleView.setText(title);
		titleView.setTextColor(Color.GRAY);
		titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);

		linearLayout.addView(titleView);

		imgView = new ImageView(getContext());
		imgView.setImageDrawable(img);
		imgView.setScaleType(ImageView.ScaleType.CENTER);

		LinearLayout.LayoutParams imgLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
		imgLayoutParams.weight = 1;

		linearLayout.addView(imgView, imgLayoutParams);
	}

	@Override
	public void setOnClickListener(@Nullable OnClickListener l) {
		super.setOnClickListener(l);
	}
}

/*
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <TextView
            android:id="@+id/function_name"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:padding="4dp"
            android:text="functionName"
            android:textColor="@color/black"
            android:textSize="13sp" />

        <ImageView
            android:id="@+id/function_image"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:scaleType="center"
            android:src="@android:mipmap/sym_def_app_icon" />

    </LinearLayout>
 */