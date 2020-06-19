package com.zerodsoft.scheduleweather.CalendarView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.zerodsoft.scheduleweather.DayFragment;


public class WeekViewPagerAdapter extends PagerAdapter implements WeekView.WeekViewInterface
{
    private static final String ADAPTER_TAG = "WEEKVIEWPAGER_ADAPTER";
    private Context context;
    private Fragment fragment;
    private ViewGroup container;


    public WeekViewPagerAdapter()
    {

    }


    public WeekViewPagerAdapter(Context context, Fragment fragment)
    {
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        View weekView = null;
        this.container = container;

        if (context != null)
        {
            weekView = new WeekView(context, WeekViewPagerAdapter.this);
        }
        container.addView(weekView);
        return weekView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        container.removeView((View) object);
    }

    @Override
    public int getCount()
    {
        return DayFragment.WEEK_NUMBER;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
    {
        return (view == (View) object);
    }


    @Override
    public void refreshSideView()
    {
        for (int i = 0; i < container.getChildCount(); i++)
        {
            container.getChildAt(i).invalidate();
        }
    }

}
