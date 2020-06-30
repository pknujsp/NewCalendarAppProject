package com.zerodsoft.scheduleweather.CalendarView;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.zerodsoft.scheduleweather.DayFragment;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Room.DBController;
import com.zerodsoft.scheduleweather.Room.DTO.GoogleScheduleDTO;
import com.zerodsoft.scheduleweather.Room.DTO.LocalScheduleDTO;

import java.util.List;


public class WeekViewPagerAdapter extends PagerAdapter implements WeekView.OnRefreshChildViewListener
{
    private static final String ADAPTER_TAG = "WEEKVIEWPAGER_ADAPTER";
    private Context context;
    private HoursView hoursView;
    private ViewGroup container;
    private WeekDatesView weekDatesView;
    private SparseArray<View> weekViewSparseArray = new SparseArray<>();
    private SparseArray<View> headerViewSparseArray = new SparseArray<>();
    private DBController dbController;


    public WeekViewPagerAdapter()
    {

    }


    public WeekViewPagerAdapter(Context context, WeekDatesView weekDatesView, HoursView hoursView)
    {
        this.context = context;
        this.weekDatesView = weekDatesView;
        this.hoursView = hoursView;
        this.dbController = DBController.getInstance();
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
            weekViewSparseArray.get(position - 1).invalidate();
            weekViewSparseArray.get(position + 1).invalidate();
        } catch (NullPointerException e)
        {

        }
    }

    public void refreshView(int position)
    {
        weekViewSparseArray.get(position).invalidate();
    }

    public void updateLayout(int eventMaxNum, int position)
    {
        ((WeekHeaderView) headerViewSparseArray.get(position)).setEventsNum(eventMaxNum);
    }

    public void getScheduleList(int position)
    {
        long sundayMillisec = 0L;
        dbController.selectScheduleData();
        sendScheduleList(position, dbController.getGoogleScheduleList(), dbController.getLocalScheduleList());
    }

    public void sendScheduleList(int position, List<GoogleScheduleDTO> googleScheduleList, List<LocalScheduleDTO> localScheduleList)
    {
        ((WeekHeaderView) headerViewSparseArray.get(position)).setScheduleList(localScheduleList, googleScheduleList);
        ((WeekView) weekViewSparseArray.get(position)).setScheduleList(localScheduleList, googleScheduleList);
    }
}
