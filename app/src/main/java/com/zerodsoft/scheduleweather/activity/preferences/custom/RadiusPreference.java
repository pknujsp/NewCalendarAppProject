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

    public RadiusPreference(Context context)
    {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder)
    {
        super.onBindViewHolder(holder);
        if (radiusTextView == null)
        {
            radiusTextView = new TextView(getContext());
            radiusTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            radiusTextView.setTextColor(Color.BLACK);
            radiusTextView.setText(getText() + "M");

            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getContext().getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
            layoutParams.gravity = Gravity.RIGHT;

            radiusTextView.setLayoutParams(layoutParams);

            ViewGroup layoutWidget = (ViewGroup) holder.findViewById(R.id.layout_widget_root);
            layoutWidget.addView(radiusTextView);
        } else
        {
            ViewGroup layoutWidget = (ViewGroup) holder.findViewById(R.id.layout_widget_root);
            if (layoutWidget.getChildCount() == 0)
            {
                ViewGroup parentViewGroup = (ViewGroup) radiusTextView.getParent();
                parentViewGroup.removeView(radiusTextView);
                layoutWidget.addView(radiusTextView);
            }
        }
    }

    public void setValue()
    {
        if (radiusTextView != null)
        {
            radiusTextView.setText(getText() + "M");
        }
    }
}
