package com.zerodsoft.scheduleweather.calendarview.week;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.activity.main.AppMainActivity;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.R;

import java.util.Calendar;
import java.util.List;


public class WeekFragment extends Fragment implements IRefreshView
{
    public static final String TAG = "WEEK_FRAGMENT";
    private ViewPager2 weekViewPager;
    private WeekViewPagerAdapter weekViewPagerAdapter;
    private int currentPosition = EventTransactionFragment.FIRST_VIEW_POSITION;

    private IControlEvent iControlEvent;
    private IToolbar iToolbar;
    private OnEventItemClickListener onEventItemClickListener;
    private static final int COLUMN_WIDTH = AppMainActivity.getDisplayWidth() / 8;

    private OnPageChangeCallback onPageChangeCallback;

    public WeekFragment(Fragment fragment, IToolbar iToolbar)
    {
        this.iControlEvent = (IControlEvent) fragment;
        this.onEventItemClickListener = (OnEventItemClickListener) fragment;
        this.iToolbar = iToolbar;
    }

    public static int getColumnWidth()
    {
        return COLUMN_WIDTH;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_week, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        weekViewPager = (ViewPager2) view.findViewById(R.id.week_viewpager);
        weekViewPagerAdapter = new WeekViewPagerAdapter(iControlEvent, onEventItemClickListener, iToolbar);
        weekViewPager.setAdapter(weekViewPagerAdapter);
        weekViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);

        onPageChangeCallback = new OnPageChangeCallback(weekViewPagerAdapter.getCALENDAR());
        weekViewPager.registerOnPageChangeCallback(onPageChangeCallback);
    }

    public void setViewPagerSwipe(boolean value)
    {
        weekViewPager.setUserInputEnabled(!value);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    class OnPageChangeCallback extends ViewPager2.OnPageChangeCallback
    {
        private final Calendar calendar;
        private Calendar copiedCalendar;

        public OnPageChangeCallback(Calendar calendar)
        {
            this.calendar = calendar;
        }


        @Override
        public void onPageScrollStateChanged(int state)
        {
            super.onPageScrollStateChanged(state);
        }


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            // 오른쪽(이전 주)으로 드래그시 positionOffset의 값이 작아짐 0.99999 -> 0.0
            // 왼쪽(다음 주)으로 드래그시 positionOffset의 값이 커짐 0.00001 -> 1.0
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position)
        {
            // drag 성공 시에만 SETTLING 직후 호출
            super.onPageSelected(position);
            currentPosition = position;

            copiedCalendar = (Calendar) calendar.clone();
            copiedCalendar.add(Calendar.WEEK_OF_YEAR, currentPosition - EventTransactionFragment.FIRST_VIEW_POSITION);

            iToolbar.setMonth(copiedCalendar.getTime());
            // 일정 목록을 가져와서 표시함 header view, week view
            weekViewPager.setUserInputEnabled(false);
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(400);
                        weekViewPager.setUserInputEnabled(true);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    public void goToToday()
    {
        if (currentPosition != EventTransactionFragment.FIRST_VIEW_POSITION)
        {
            weekViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);
        }
    }


    @Override
    public void refreshView()
    {
        int currentItem = weekViewPager.getCurrentItem();
        long start = weekViewPagerAdapter.getDate(currentItem, 0).getTime();
        long end = weekViewPagerAdapter.getDate(currentItem, 7).getTime();

        iControlEvent.getInstances(currentItem, start, end, new EventCallback<List<CalendarInstance>>()
        {
            @Override
            public void onResult(List<CalendarInstance> e)
            {
                weekViewPagerAdapter.refresh(currentItem, e);
                weekViewPagerAdapter.notifyDataSetChanged();
            }
        });
    }
}