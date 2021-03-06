package com.zerodsoft.scheduleweather.activity.preferences.custom;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

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
            ViewGroup viewGroup = holder.findViewById()
        }
    }

    public void setTimeZone(TimeZone timeZone)
    {
        this.timeZone = timeZone;
    }
}
