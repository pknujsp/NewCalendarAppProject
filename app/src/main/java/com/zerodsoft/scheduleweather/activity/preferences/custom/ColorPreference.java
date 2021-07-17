package com.zerodsoft.scheduleweather.activity.preferences.custom;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.preferences.fragments.CalendarColorFragment;
import com.zerodsoft.scheduleweather.activity.preferences.interfaces.PreferenceListener;
import com.zerodsoft.scheduleweather.event.util.EventUtil;

public class ColorPreference extends Preference {
	private View colorView;
	private ContentValues calendar;
	private CalendarColorFragment.ColorPreferenceInterface colorPreferenceInterface;

	public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ColorPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ColorPreference(Context context, CalendarColorFragment.ColorPreferenceInterface colorPreferenceInterface, ContentValues calendar) {
		super(context);
		this.colorPreferenceInterface = colorPreferenceInterface;
		this.calendar = calendar;
	}

	@Override
	public void onBindViewHolder(PreferenceViewHolder holder) {
		super.onBindViewHolder(holder);
		if (colorView == null) {
			colorView = new View(getContext());
			colorView.setBackgroundColor(EventUtil.getColor(calendar.getAsInteger(CalendarContract.Calendars.CALENDAR_COLOR)));
			int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getContext().getResources().getDisplayMetrics());
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
			layoutParams.gravity = Gravity.CENTER;

			ViewGroup layoutWidget = (ViewGroup) holder.findViewById(R.id.layout_widget_root);
			layoutWidget.addView(colorView, layoutParams);
			colorPreferenceInterface.onCreatedPreferenceView(calendar, this);
		}
	}

	public void setColor(int color) {
		colorView.setBackgroundColor(EventUtil.getColor(color));
	}

	public ContentValues getCalendar() {
		return calendar;
	}
}
