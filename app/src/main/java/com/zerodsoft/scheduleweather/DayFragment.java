package com.zerodsoft.scheduleweather;

import android.graphics.Point;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zerodsoft.scheduleweather.CalendarView.HoursView;
import com.zerodsoft.scheduleweather.CalendarView.MoveWeekListener;
import com.zerodsoft.scheduleweather.CalendarView.WeekDatesView;
import com.zerodsoft.scheduleweather.CalendarView.WeekViewPagerAdapter;


public class DayFragment extends Fragment
{
    //view
    private static final String DAYFRAGMENT_TAG = "DAY_FRAGMENT";
    private WeekDatesView mWeekDatesView;
    private HoursView hoursView;
    private LinearLayout leftLayout;
    private LinearLayout rightLayout;
    private ViewPager weekViewPager;
    private WeekViewPagerAdapter weekViewPagerAdapter;
    private int spacingBetweenDay;

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
        Point point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getRealSize(point);
        spacingBetweenDay = point.x / 8;
        assignViews(view);
        return view;
    }


    private void assignViews(View view)
    {

        leftLayout = (LinearLayout) view.findViewById(R.id.leftview_layout);
        rightLayout = (LinearLayout) view.findViewById(R.id.rightview_layout);

        leftLayout.setLayoutParams(new LinearLayout.LayoutParams(spacingBetweenDay, ViewGroup.LayoutParams.MATCH_PARENT));
        leftLayout.invalidate();
        rightLayout.setLayoutParams(new LinearLayout.LayoutParams(spacingBetweenDay * 7, ViewGroup.LayoutParams.MATCH_PARENT));
        rightLayout.invalidate();

        mWeekDatesView = (WeekDatesView) view.findViewById(R.id.weekdatesview);
        hoursView = (HoursView) view.findViewById(R.id.hoursview);

        //  changeListener changeListener = new changeListener();

        weekViewPager = (ViewPager) view.findViewById(R.id.weekviewpager);
        weekViewPagerAdapter = new WeekViewPagerAdapter(getContext(), mWeekDatesView, hoursView);
        weekViewPager.setAdapter(weekViewPagerAdapter);
        weekViewPager.setCurrentItem(FIRST_VIEW_NUMBER);
        // weekViewPager.addOnPageChangeListener(changeListener);


    }

    class changeListener extends ViewPager.SimpleOnPageChangeListener
    {
        MoveWeekListener moveWeekListener;
        float lastPositionOffset = 0f;
        boolean isLeftDrag = false;
        boolean isRightDrag = false;

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
            // 왼쪽(다음 주)으로 드래그시 positionOffset의 값이 작아짐 0.99999 -> 0.0
            // 오른쪽(이전 주)으로 드래그시 positionOffset의 값이 커짐 0.00001 -> 1.0

            if (!isLeftDrag || !isRightDrag)
            {
                if (positionOffset > lastPositionOffset)
                {
                    // 이전 주로 이동
                    isRightDrag = true;
                } else
                {
                    // 다음 주로 이동
                    isLeftDrag = true;
                }
            }

            lastPositionOffset = positionOffset;
        }

        @Override
        public void onPageSelected(int position)
        {
            super.onPageSelected(position);
        }

    }

}