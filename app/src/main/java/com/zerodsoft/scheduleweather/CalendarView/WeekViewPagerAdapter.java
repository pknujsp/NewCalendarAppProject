package com.zerodsoft.scheduleweather.CalendarView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.zerodsoft.scheduleweather.DayFragment;


public class WeekViewPagerAdapter extends PagerAdapter implements WeekView.WeekViewInterface
{
    private static final String ADAPTER_TAG = "WEEKVIEWPAGER_ADAPTER";
    private Context context;
    private Fragment fragment;
    private int num;
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
        View view = null;
        this.container = container;

        if (context != null)
        {
            view = new WeekView(context, WeekViewPagerAdapter.this, (DayFragment) fragment);
        }
        container.addView(view);
        return view;
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


    public float getCurrentViewY()
    {
        return WeekView.getCurrentCoordinateY();
    }

    @Override
    public void startUpdate(@NonNull ViewGroup container)
    {
        Log.e(ADAPTER_TAG, "start update");
    }


    @Override
    public void refreshSideView(int position)
    {
        container.getChildAt(1).invalidate();
        container.getChildAt(2).invalidate();

    }

}
