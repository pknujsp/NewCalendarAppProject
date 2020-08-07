package com.zerodsoft.scheduleweather.CalendarFragment;

import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.CalendarView.HoursView;
import com.zerodsoft.scheduleweather.CalendarView.Week.WeekDatesView;
import com.zerodsoft.scheduleweather.CalendarView.Week.WeekViewPagerAdapter;
import com.zerodsoft.scheduleweather.R;

import java.util.Calendar;


public class WeekFragment extends Fragment
{
    public static final String TAG = "WEEK_FRAGMENT";

    private RelativeLayout weekDatesLayout;
    private TextView weekDatesTextView;
    private ImageButton weekDatesButton;
    private HoursView hoursView;

    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout headerLayout;
    private RelativeLayout contentLayout;

    private ViewPager2 weekViewPager;
    private WeekViewPagerAdapter weekViewPagerAdapter;
    private int spacingBetweenDay;
    private OnPageChangeCallback onPageChangeCallback;

    public static final int WEEK_TOTAL_COUNT = 521;
    public static final int FIRST_VIEW_NUMBER = 261;

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
        spacingBetweenDay = point.x / 8;
        return inflater.inflate(R.layout.fragment_week, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.week_coordinator_layout);
        headerLayout = (RelativeLayout) view.findViewById(R.id.week_header_layout);
        contentLayout = (RelativeLayout) view.findViewById(R.id.week_content_layout);
        weekDatesLayout = (RelativeLayout) view.findViewById(R.id.week_dates_layout);
        hoursView = (HoursView) view.findViewById(R.id.week_hours_view);

        weekDatesTextView = (TextView) view.findViewById(R.id.week_dates_textview);
        weekDatesButton = (ImageButton) view.findViewById(R.id.week_dates_button);

        onPageChangeCallback = new OnPageChangeCallback();

        weekViewPager = (ViewPager2) view.findViewById(R.id.week_viewpager);
        weekViewPagerAdapter = new WeekViewPagerAdapter(getActivity(), hoursView);
        weekViewPager.setAdapter(weekViewPagerAdapter);
        weekViewPager.setCurrentItem(FIRST_VIEW_NUMBER);
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
        private int firstPosition = WeekFragment.FIRST_VIEW_NUMBER;
        private int position;
        private Calendar today = Calendar.getInstance();

        public void initFirstPosition()
        {
            this.firstPosition = WeekFragment.FIRST_VIEW_NUMBER;
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
            if (state == ViewPager.SCROLL_STATE_IDLE)
            {
            } else if (state == ViewPager.SCROLL_STATE_DRAGGING)
            {
                Log.e(TAG, "SCROLL_STATE_DRAGGING");
                weekViewPagerAdapter.refreshChildView(this.position);
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
            Log.e(TAG, "onPageSelected" + Integer.toString(position));
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
            mWeekDatesView.updateViewHeight(weekViewPagerAdapter.getEventRowNum(finalPosition));
        }

        public int getFirstPosition()
        {
            return firstPosition;
        }
    }

    public void goToToday()
    {
        int currentPosition = onPageChangeCallback.getFirstPosition();

        if (currentPosition != WeekFragment.FIRST_VIEW_NUMBER)
        {
            weekViewPager.setCurrentItem(WeekFragment.FIRST_VIEW_NUMBER, true);
            weekViewPagerAdapter.notifyDataSetChanged();
        }
    }
}