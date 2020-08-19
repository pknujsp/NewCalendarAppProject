package com.zerodsoft.scheduleweather.CalendarFragment;

import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.CalendarView.ViewModel.WeekViewModel;
import com.zerodsoft.scheduleweather.CalendarView.Week.WeekViewPagerAdapter;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Room.DTO.ScheduleDTO;

import java.util.Calendar;
import java.util.List;


public class WeekFragment extends Fragment
{
    public static final String TAG = "WEEK_FRAGMENT";
    private ViewPager2 weekViewPager;
    private WeekViewPagerAdapter weekViewPagerAdapter;

    private static int SPACING_BETWEEN_DAY = 0;
    private static int DISPLAY_WIDTH = 0;
    private static int DISPLAY_HEIGHT = 0;

    private OnPageChangeCallback onPageChangeCallback;

    public WeekFragment()
    {

    }

    public static int getSpacingBetweenDay()
    {
        return SPACING_BETWEEN_DAY;
    }

    public static int getDisplayHeight()
    {
        return DISPLAY_HEIGHT;
    }

    public static int getDisplayWidth()
    {
        return DISPLAY_WIDTH;
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
        Point point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getRealSize(point);

        SPACING_BETWEEN_DAY = point.x / 8;
        DISPLAY_WIDTH = point.x;
        DISPLAY_HEIGHT = point.y;

        return inflater.inflate(R.layout.fragment_week, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        onPageChangeCallback = new OnPageChangeCallback();

        weekViewPager = (ViewPager2) view.findViewById(R.id.week_viewpager);
        weekViewPagerAdapter = new WeekViewPagerAdapter(getActivity(), this);
        weekViewPager.setAdapter(weekViewPagerAdapter);
        weekViewPager.setCurrentItem(WeekViewPagerAdapter.FIRST_VIEW_NUMBER, false);
        weekViewPager.registerOnPageChangeCallback(onPageChangeCallback);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    class OnPageChangeCallback extends ViewPager2.OnPageChangeCallback
    {
        private int currentPosition = WeekViewPagerAdapter.FIRST_VIEW_NUMBER;

        public int getCurrentPosition()
        {
            return currentPosition;
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
            // 일정 목록을 가져와서 표시함 header view, week view
            weekViewPagerAdapter.selectEvents(position);
        }

    }


    public void goToToday()
    {
        if (onPageChangeCallback.getCurrentPosition() != WeekViewPagerAdapter.FIRST_VIEW_NUMBER)
        {
            weekViewPager.setCurrentItem(WeekViewPagerAdapter.FIRST_VIEW_NUMBER, true);
            weekViewPagerAdapter.notifyDataSetChanged();
        }
    }
}