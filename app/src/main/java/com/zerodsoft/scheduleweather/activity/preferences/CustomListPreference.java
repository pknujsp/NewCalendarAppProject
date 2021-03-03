package com.zerodsoft.scheduleweather.activity.preferences;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.AsyncListUtil;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.preferences.interfaces.PreferenceListener;

public class CustomListPreference extends ListPreference
{
    private View customView;
    private PreferenceListener preferenceListener;

    public CustomListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomListPreference(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public CustomListPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomListPreference(Context context)
    {
        super(context);
    }

    public void setPreferenceListener(PreferenceListener preferenceListener)
    {
        this.preferenceListener = preferenceListener;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder)
    {
        //부모 프래그먼트의 onResume()이후 호출된다
        super.onBindViewHolder(holder);
        customView = new View(getContext());
        int dp32 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, getContext().getResources().getDisplayMetrics());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp32, dp32);
        layoutParams.gravity = Gravity.CENTER;

        ViewGroup widgetLayout = (ViewGroup) holder.findViewById(R.id.layout_widget_root);
        widgetLayout.addView(customView, layoutParams);
        preferenceListener.onCreatedPreferenceView();
    }

    public View getCustomView()
    {
        return customView;
    }
}
