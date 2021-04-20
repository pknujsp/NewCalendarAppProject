package com.zerodsoft.scheduleweather.common.classes;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.zerodsoft.scheduleweather.common.interfaces.OnChangedVisibilityListener;

public class CustomProgressbar extends ProgressBar
{
    private OnChangedVisibilityListener onChangedVisibilityListener;

    public CustomProgressbar(Context context)
    {
        super(context);
    }

    public CustomProgressbar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomProgressbar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public CustomProgressbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setVisibility(int visibility)
    {
        super.setVisibility(visibility);
        if (onChangedVisibilityListener != null)
        {
            onChangedVisibilityListener.onChangedVisibility(visibility);
        }
    }

    public void setOnChangedVisibilityListener(OnChangedVisibilityListener onChangedVisibilityListener)
    {
        this.onChangedVisibilityListener = onChangedVisibilityListener;
    }

    public void removeOnChangedVisibilityListener()
    {
        this.onChangedVisibilityListener = null;
    }
}
