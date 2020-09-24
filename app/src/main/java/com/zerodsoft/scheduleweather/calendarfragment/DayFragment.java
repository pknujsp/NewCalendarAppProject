package com.zerodsoft.scheduleweather.calendarfragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.day.DayViewPagerAdapter;
import com.zerodsoft.scheduleweather.calendarview.week.WeekViewPagerAdapter;

import java.util.Calendar;

public class DayFragment extends Fragment
{
    public static final String TAG = "DAY_FRAGMENT";
    public static final int firstViewPosition = Integer.MAX_VALUE / 2;

    private ViewPager2 dayViewPager;
    private DayViewPagerAdapter dayViewPagerAdapter;
    private Calendar calendar;

    public DayFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        calendar = Calendar.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        dayViewPager = (ViewPager2) view.findViewById(R.id.day_viewpager);

        dayViewPagerAdapter = new DayViewPagerAdapter(this, (Calendar) calendar.clone());
        dayViewPager.setAdapter(dayViewPagerAdapter);
        dayViewPager.setCurrentItem(firstViewPosition, false);

        OnPageChangeCallback onPageChangeCallback = new OnPageChangeCallback();
        dayViewPager.registerOnPageChangeCallback(onPageChangeCallback);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    class OnPageChangeCallback extends ViewPager2.OnPageChangeCallback
    {
        private int currentPosition = firstViewPosition;

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
            // 일정 목록을 가져와서 표시함 header view, week view
            dayViewPager.setUserInputEnabled(false);
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(400);
                        dayViewPager.setUserInputEnabled(true);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }
}
