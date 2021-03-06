package com.zerodsoft.scheduleweather.activity.preferences.custom;

import android.content.Context;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceViewHolder;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.util.EventUtil;

public class RadiusPreference extends EditTextPreference
{
    private TextView radiusTextView;
    private String value;

    public RadiusPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public RadiusPreference(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public RadiusPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RadiusPreference(Context context, String value)
    {
        super(context);
        this.value = value;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder)
    {
        super.onBindViewHolder(holder);
        if (radiusTextView == null)
        {
            radiusTextView = new TextView(getContext());
            radiusTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
            radiusTextView.setTextColor(Color.BLACK);
            radiusTextView.setText(value);

            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, getContext().getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getContext().getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            layoutParams.gravity = Gravity.RIGHT;

            ViewGroup layoutWidget = (ViewGroup) holder.findViewById(R.id.layout_widget_root);
            layoutWidget.addView(radiusTextView, layoutParams);
        }
    }

    public void setValue(String value)
    {
        this.value = value;
        radiusTextView.setText(value);
    }
}
