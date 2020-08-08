package com.zerodsoft.scheduleweather.CalendarFragment;

import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.CalendarView.Week.WeekViewPagerAdapter;
import com.zerodsoft.scheduleweather.R;

import java.util.Calendar;


public class WeekFragment extends Fragment
{
    public static final String TAG = "WEEK_FRAGMENT";
    private ViewPager2 weekViewPager;
    private WeekViewPagerAdapter weekViewPagerAdapter;

    public static int SPACING_BETWEEN_DAY;
    public static int DISPLAY_WIDTH;
    public static int DISPLAY_HEIGHT;

    private OnPageChangeCallback onPageChangeCallback;

    public WeekFragment()
    {
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
        weekViewPagerAdapter = new WeekViewPagerAdapter(getActivity());

        weekViewPager.setAdapter(weekViewPagerAdapter);
        weekViewPager.setCurrentItem(WeekViewPagerAdapter.FIRST_VIEW_NUMBER);
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
        private int finalPosition;
        private int firstPosition = WeekViewPagerAdapter.FIRST_VIEW_NUMBER;
        private int position;
        private Calendar today = Calendar.getInstance();

        public void initFirstPosition()
        {
            this.firstPosition = WeekViewPagerAdapter.FIRST_VIEW_NUMBER;
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
            if (state == ViewPager.SCROLL_STATE_IDLE)
            {
            } else if (state == ViewPager.SCROLL_STATE_DRAGGING)
            {
                //   Log.e(TAG, "SCROLL_STATE_DRAGGING");
                //  weekViewPagerAdapter.refreshChildView(this.position);
            } else if (state == ViewPager.SCROLL_STATE_SETTLING)
            {
            }
        }


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            // 오른쪽(이전 주)으로 드래그시 positionOffset의 값이 작아짐 0.99999 -> 0.0
            // 왼쪽(다음 주)으로 드래그시 positionOffset의 값이 커짐 0.00001 -> 1.0
            this.position = position;
        }

        @Override
        public void onPageSelected(int position)
        {
            // drag 성공 시에만 SETTLING 직후 호출
            super.onPageSelected(position);
            // Log.e(TAG, "onPageSelected" + Integer.toString(position));
            finalPosition = position;

            if (finalPosition < firstPosition)
            {
                // 이전 주
                today.add(Calendar.WEEK_OF_YEAR, -1);
            } else
            {
                // 다음 주
                today.add(Calendar.WEEK_OF_YEAR, 1);
            }
            firstPosition = finalPosition;
        }

        public int getFirstPosition()
        {
            return firstPosition;
        }
    }


    public void goToToday()
    {
        int currentPosition = onPageChangeCallback.getFirstPosition();

        if (currentPosition != WeekViewPagerAdapter.FIRST_VIEW_NUMBER)
        {
            weekViewPager.setCurrentItem(WeekViewPagerAdapter.FIRST_VIEW_NUMBER, true);
            weekViewPagerAdapter.notifyDataSetChanged();
        }
    }
}