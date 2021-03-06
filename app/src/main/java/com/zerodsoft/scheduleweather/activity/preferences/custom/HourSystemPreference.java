package com.zerodsoft.scheduleweather.activity.preferences.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.zerodsoft.scheduleweather.R;

public class HourSystemPreference extends ListPreference
{
    private String selectedHourSystem;
    private TextView hourSystemTextView;

    public HourSystemPreference(Context context, String selectedHourSystem)
    {
        super(context);
        this.selectedHourSystem = selectedHourSystem;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder)
    {
        super.onBindViewHolder(holder);
        if (hourSystemTextView == null)
        {
            hourSystemTextView = new TextView(getContext());
            hourSystemTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            hourSystemTextView.setTextColor(Color.BLACK);
            hourSystemTextView.setText(selectedHourSystem);

            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, getContext().getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getContext().getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.RIGHT;

            ViewGroup layoutWidget = (ViewGroup) holder.findViewById(R.id.layout_widget_root);
            layoutWidget.addView(hourSystemTextView, layoutParams);
        }
    }

    public void setSelectedHourSystem(String selectedHourSystem)
    {
        this.selectedHourSystem = selectedHourSystem;
        hourSystemTextView.setText(selectedHourSystem);
    }
}
