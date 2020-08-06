package com.zerodsoft.scheduleweather.CalendarView.Week;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.zerodsoft.scheduleweather.CalendarFragment.WeekFragment;
import com.zerodsoft.scheduleweather.CalendarView.HoursView;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;

import java.util.List;


public class WeekViewPagerAdapter extends PagerAdapter implements WeekView.OnRefreshChildViewListener
{
    public static final String TAG = "WEEKVIEWPAGER_ADAPTER";
    private Context context;
    private HoursView hoursView;
    private ViewGroup container;
    private WeekDatesView weekDatesView;
    private SparseArray<View> weekViewSparseArray = new SparseArray<>();
    private SparseArray<View> headerViewSparseArray = new SparseArray<>();

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
        weekViewSparseArray.put(position, weekView);
        headerViewSparseArray.put(position, headerView);

        container.addView(rootView);
        return rootView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        weekViewSparseArray.remove(position);
        headerViewSparseArray.remove(position);
        container.removeView((View) object);
    }

    @Override
    public int getCount()
    {
        return WeekFragment.WEEK_NUMBER;
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
            weekViewSparseArray.get(position - 1).invalidate();
            weekViewSparseArray.get(position + 1).invalidate();
        } catch (NullPointerException e)
        {

        }
    }


    public int getEventRowNum(int position)
    {
        return ((WeekHeaderView) headerViewSparseArray.get(position)).getEventRowNum();
    }
}
