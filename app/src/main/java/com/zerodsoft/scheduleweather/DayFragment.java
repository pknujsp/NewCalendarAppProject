package com.zerodsoft.scheduleweather;

import android.graphics.Point;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zerodsoft.scheduleweather.CalendarView.HoursView;
import com.zerodsoft.scheduleweather.CalendarView.WeekDatesView;
import com.zerodsoft.scheduleweather.CalendarView.WeekViewPagerAdapter;

import java.util.Calendar;


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
    private ChangeListener changeListener;

    public static final int WEEK_NUMBER = 521;
    public static final int FIRST_VIEW_NUMBER = 261;


    public DayFragment()
    {
    }

    public interface OnUpdateWeekDatesListener
    {
        void updateWeekDates(String week);
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

        changeListener = new ChangeListener().setOnUpdateWeekDatesListener(mWeekDatesView);

        weekViewPager = (ViewPager) view.findViewById(R.id.weekviewpager);
        weekViewPagerAdapter = new WeekViewPagerAdapter(getContext(), mWeekDatesView, hoursView);
        weekViewPager.setAdapter(weekViewPagerAdapter);
        weekViewPager.setCurrentItem(FIRST_VIEW_NUMBER);
        weekViewPager.addOnPageChangeListener(changeListener);
    }

    class ChangeListener extends ViewPager.SimpleOnPageChangeListener
    {
        private int finalPosition;
        private int firstPosition = DayFragment.FIRST_VIEW_NUMBER;
        private int position;
        private int scrollingState;
        private Calendar today = Calendar.getInstance();
        private OnUpdateWeekDatesListener onUpdateWeekDatesListener;

        public void initFirstPosition()
        {
            this.firstPosition = DayFragment.FIRST_VIEW_NUMBER;
        }

        public ChangeListener setOnUpdateWeekDatesListener(OnUpdateWeekDatesListener onUpdateWeekDatesListener)
        {
            this.onUpdateWeekDatesListener = onUpdateWeekDatesListener;
            return this;
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
            if (state == ViewPager.SCROLL_STATE_IDLE)
            {
                scrollingState = ViewPager.SCROLL_STATE_IDLE;
            } else if (state == ViewPager.SCROLL_STATE_DRAGGING)
            {
                Log.e(DAYFRAGMENT_TAG, "SCROLL_STATE_DRAGGING");
                scrollingState = ViewPager.SCROLL_STATE_DRAGGING;
                weekViewPagerAdapter.refreshChildView(position);
            } else if (state == ViewPager.SCROLL_STATE_SETTLING)
            {
                scrollingState = ViewPager.SCROLL_STATE_SETTLING;
            }
        }


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            // 오른쪽(이전 주)으로 드래그시 positionOffset의 값이 작아짐 0.99999 -> 0.0
            // 왼쪽(다음 주)으로 드래그시 positionOffset의 값이 커짐 0.00001 -> 1.0
            Log.e(DAYFRAGMENT_TAG, "onPageScrolled" + Float.toString(positionOffset));
            this.position = position;
        }

        @Override
        public void onPageSelected(int position)
        {
            // drag 성공 시에만 SETTLING 직후 호출
            super.onPageSelected(position);
            Log.e(DAYFRAGMENT_TAG, "onPageSelected" + Integer.toString(position));
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
            onUpdateWeekDatesListener.updateWeekDates(Integer.toString(today.get(Calendar.WEEK_OF_YEAR)) + "주");
        }

        public int getFirstPosition()
        {
            return firstPosition;
        }
    }

    public void goToToday()
    {
        int currentPosition = changeListener.getFirstPosition();

        if (currentPosition != DayFragment.FIRST_VIEW_NUMBER)
        {
            if (currentPosition < DayFragment.FIRST_VIEW_NUMBER)
            {
                for (int i = DayFragment.FIRST_VIEW_NUMBER - currentPosition; i > 0; i--)
                {
                    weekViewPager.arrowScroll(View.FOCUS_RIGHT);
                }
            } else
            {
                for (int i = currentPosition - DayFragment.FIRST_VIEW_NUMBER; i > 0; i--)
                {
                    weekViewPager.arrowScroll(View.FOCUS_LEFT);
                }
            }
            weekViewPagerAdapter.notifyDataSetChanged();
        }
    }
}