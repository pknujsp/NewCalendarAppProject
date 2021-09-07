package com.zerodsoft.calendarplatform.activity.preferences.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.zerodsoft.calendarplatform.R;

import java.util.Locale;
import java.util.TimeZone;

public class TimeZonePreference extends Preference
{
    private TextView timezoneTextView;
    private TimeZone timeZone;

    public TimeZonePreference(Context context)
    {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder)
    {
        super.onBindViewHolder(holder);
        if (timezoneTextView == null)
        {
            timezoneTextView = new TextView(getContext());
            timezoneTextView.setText(timeZone.getDisplayName(Locale.KOREAN));
            timezoneTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            timezoneTextView.setTextColor(Color.BLACK);

            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getContext().getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
            layoutParams.gravity = Gravity.RIGHT;
            timezoneTextView.setLayoutParams(layoutParams);

            LinearLayout viewGroup = (LinearLayout) holder.findViewById(R.id.layout_widget_root);
            viewGroup.addView(timezoneTextView);
        } else
        {
            LinearLayout viewGroup = (LinearLayout) holder.findViewById(R.id.layout_widget_root);
            if (viewGroup.getChildCount() == 0)
            {
                ViewGroup parent = (ViewGroup) timezoneTextView.getParent();
                parent.removeView(timezoneTextView);
                viewGroup.addView(timezoneTextView);
            }
        }
    }

    public void setTimeZone(TimeZone timeZone)
    {
        this.timeZone = timeZone;
        if (timezoneTextView != null)
        {
            timezoneTextView.setText(timeZone.getDisplayName());
        }
    }

    public TimeZone getTimeZone()
    {
        return timeZone;
    }
}
