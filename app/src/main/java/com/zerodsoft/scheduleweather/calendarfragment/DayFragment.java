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
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.Date;
import java.util.List;

public class DayFragment extends Fragment
{
    public static final String TAG = "DAY_FRAGMENT";

    private ViewPager2 dayViewPager;
    private DayViewPagerAdapter dayViewPagerAdapter;
    private OnControlEvent onControlEvent;

    public DayFragment(Fragment fragment)
    {
        onControlEvent = (OnControlEvent) fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_day, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        dayViewPager = (ViewPager2) view.findViewById(R.id.day_viewpager);

        dayViewPagerAdapter = new DayViewPagerAdapter(DayFragment.this);
        dayViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        dayViewPager.setAdapter(dayViewPagerAdapter);
        dayViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);

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
        private int currentPosition = EventTransactionFragment.FIRST_VIEW_POSITION;
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

    public void onSelectedSchedules(int viewPosition, List<ScheduleDTO> schedules)
    {
        dayViewPagerAdapter.setData(viewPosition, schedules);
    }

    public void requestSchedules(int position, Date startDate, Date endDate)
    {
        // 해당 페이지에 해당하는 날짜에 대한 데이터 불러오기
        onControlEvent.requestSchedules(this, position, startDate, endDate);
    }

    public void setMonth(Date date)
    {
        onControlEvent.setToolbarMonth(date);
    }

    public ViewPager2 getDayViewPager()
    {
        return dayViewPager;
    }

    public void showSchedule(int scheduleId)
    {
        onControlEvent.showSchedule(scheduleId);
    }
}
