package com.zerodsoft.scheduleweather.calendarfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.day.DayViewPagerAdapter;
import com.zerodsoft.scheduleweather.calendarview.month.MonthViewPagerAdapter;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.Date;
import java.util.List;

public class MonthFragment extends Fragment
{
    public static final String TAG = "MonthFragment";

    private OnControlCalendar onControlCalendar;
    private ViewPager2 viewPager;
    private MonthViewPagerAdapter viewPagerAdapter;

    public MonthFragment(Fragment fragment)
    {
        onControlCalendar = (OnControlCalendar) fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_month, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        viewPager = (ViewPager2) view.findViewById(R.id.month_viewpager);

        viewPagerAdapter = new MonthViewPagerAdapter(this);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(CalendarTransactionFragment.FIRST_VIEW_POSITION, false);

        OnPageChangeCallback onPageChangeCallback = new OnPageChangeCallback();
        viewPager.registerOnPageChangeCallback(onPageChangeCallback);

        super.onViewCreated(view, savedInstanceState);
    }

    public void onSelectedSchedules(int viewPosition, List<ScheduleDTO> schedules)
    {
        viewPagerAdapter.setData(viewPosition, schedules);
    }
    public void setMonth(Date date)
    {
        onControlCalendar.setToolbarMonth(date);
    }
    public void requestSchedules(int position, Date startDate, Date endDate)
    {
        // 해당 페이지에 해당하는 날짜에 대한 데이터 불러오기
        onControlCalendar.requestSchedules(this, position, startDate, endDate);
    }

    class OnPageChangeCallback extends ViewPager2.OnPageChangeCallback
    {
        private int currentPosition = CalendarTransactionFragment.FIRST_VIEW_POSITION;
        private int lastPosition;

        public OnPageChangeCallback()
        {
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
            lastPosition = currentPosition;
            currentPosition = position;

            super.onPageSelected(position);
        }
    }
}
