package com.zerodsoft.scheduleweather.CalendarView;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HeaderViewListAdapter;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.zerodsoft.scheduleweather.DayFragment;
import com.zerodsoft.scheduleweather.R;


public class WeekViewPagerAdapter extends PagerAdapter implements WeekView.OnRefreshChildViewListener
{
    private static final String ADAPTER_TAG = "WEEKVIEWPAGER_ADAPTER";
    private Context context;
    private HoursView hoursView;
    private ViewGroup container;
    private WeekDatesView weekDatesView;
    private SparseArray<View> viewSparseArray = new SparseArray<>();


    public WeekViewPagerAdapter()
    {

    }


    public WeekViewPagerAdapter(Context context, WeekDatesView weekDatesView, HoursView hoursView)
    {
        this.context = context;
        this.weekDatesView = weekDatesView;
        this.hoursView = hoursView;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        this.container = container;
        View rootView = null, headerView = null, weekView = null;

        if (context != null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView = layoutInflater.inflate(R.layout.weekview_viewpager_item, container, false);
            headerView = (WeekHeaderView) rootView.findViewById(R.id.weekheaderview);
            weekView = (WeekView) rootView.findViewById(R.id.weekview);
            ((WeekHeaderView) headerView).setPosition(position);
            ((WeekView) weekView).setPosition(position).setCoordinateInfoInterface((WeekHeaderView) headerView).setOnRefreshChildViewListener(this).setOnRefreshHoursViewListener(hoursView);
        }
        viewSparseArray.put(position, weekView);
        container.addView(rootView);
        return rootView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        viewSparseArray.remove(position);
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
    public void refreshChildView(int position)
    {
        try
        {
            viewSparseArray.get(position - 1).invalidate();
            viewSparseArray.get(position + 1).invalidate();
        } catch (NullPointerException e)
        {

        }
    }

    public void refreshView(int position)
    {
        viewSparseArray.get(position).invalidate();
    }

}
