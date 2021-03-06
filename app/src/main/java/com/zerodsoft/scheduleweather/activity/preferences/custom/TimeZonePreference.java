package com.zerodsoft.scheduleweather.activity.preferences.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.zerodsoft.scheduleweather.R;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.TimeZone;

public class TimeZonePreference extends Preference
{
    private TextView timezoneTextView;
    private TimeZone timeZone;

    public TimeZonePreference(Context context, TimeZone timeZone)
    {
        super(context);
        this.timeZone = timeZone;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder)
    {
        super.onBindViewHolder(holder);
        if (timezoneTextView == null)
        {
            LinearLayout viewGroup = (LinearLayout) holder.findViewById(R.id.layout_widget_root);
            timezoneTextView = new TextView(getContext());
            timezoneTextView.setText(timeZone.getDisplayName(Locale.KOREAN));
            timezoneTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            timezoneTextView.setTextColor(Color.BLACK);

            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, getContext().getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getContext().getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
            layoutParams.gravity = Gravity.RIGHT;

            viewGroup.addView(timezoneTextView, layoutParams);
        }
    }

    public void setTimeZone(TimeZone timeZone)
    {
        this.timeZone = timeZone;
        timezoneTextView.setText(timeZone.getDisplayName());
    }

    public TimeZone getTimeZone()
    {
        return timeZone;
    }
}
