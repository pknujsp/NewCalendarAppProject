package com.zerodsoft.scheduleweather;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zerodsoft.scheduleweather.CalendarView.MoveWeekListener;
import com.zerodsoft.scheduleweather.CalendarView.WeekDatesView;
import com.zerodsoft.scheduleweather.CalendarView.WeekView;
import com.zerodsoft.scheduleweather.CalendarView.WeekHeaderView;
import com.zerodsoft.scheduleweather.CalendarView.WeekViewPagerAdapter;


public class DayFragment extends Fragment
{
    //view
    private static final String DAYFRAGMENT_TAG = "DAY_FRAGMENT";
    private WeekHeaderView mWeekHeaderView;
    private WeekDatesView mWeekDatesView;
    private LinearLayout headerLayout;
    private ViewPager weekViewPager;
    private WeekViewPagerAdapter weekViewPagerAdapter;

    public static final int WEEK_NUMBER = 521;
    public static final int FIRST_VIEW_NUMBER = 261;


    public DayFragment()
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
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        assignViews(view);
        return view;
    }


    private void assignViews(View view)
    {
        headerLayout = (LinearLayout) view.findViewById(R.id.headerview_layout);
        mWeekHeaderView = (WeekHeaderView) view.findViewById(R.id.weekheaderview);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mWeekHeaderView.getMHeaderHeight()));
        headerLayout.invalidate();
        mWeekDatesView = (WeekDatesView) view.findViewById(R.id.weekdatesview);
        mWeekHeaderView.setOnUpdateWeekDatesListener(mWeekDatesView);

        changeListener changeListener = new changeListener();
        changeListener.setMoveWeekListener(mWeekHeaderView);

        weekViewPager = (ViewPager) view.findViewById(R.id.weekviewpager);
        weekViewPagerAdapter = new WeekViewPagerAdapter(getContext(), DayFragment.this);
        weekViewPager.setAdapter(weekViewPagerAdapter);
        weekViewPager.setCurrentItem(FIRST_VIEW_NUMBER);
        weekViewPager.addOnPageChangeListener(changeListener);
    }

    class changeListener extends ViewPager.SimpleOnPageChangeListener
    {
        MoveWeekListener moveWeekListener;

        public void setMoveWeekListener(MoveWeekListener moveWeekListener)
        {
            this.moveWeekListener = moveWeekListener;
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
            super.onPageScrollStateChanged(state);
            if (state == ViewPager.SCROLL_STATE_IDLE)
            {
                Log.e(DAYFRAGMENT_TAG, "SCROLL_STATE_IDLE");
            } else if (state == ViewPager.SCROLL_STATE_DRAGGING)
            {
                Log.e(DAYFRAGMENT_TAG, "SCROLL_STATE_DRAGGING");
            } else if (state == ViewPager.SCROLL_STATE_SETTLING)
            {
                Log.e(DAYFRAGMENT_TAG, "SCROLL_STATE_SETTLING");
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            // 왼쪽으로 드래그시 positionOffset의 값이 작아짐 1.0 -> 0.0
            // 오른쪽으로 드래그시 positionOffset의 값이 커짐 0.0 -> 1.0
        }

        @Override
        public void onPageSelected(int position)
        {
            super.onPageSelected(position);
        }
    }

}