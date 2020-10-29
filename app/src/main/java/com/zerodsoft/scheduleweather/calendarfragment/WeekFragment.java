package com.zerodsoft.scheduleweather.calendarfragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.AppMainActivity;
import com.zerodsoft.scheduleweather.calendarview.week.WeekViewPagerAdapter;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.Date;
import java.util.List;


public class WeekFragment extends Fragment implements OnEventItemClickListener
{
    public static final String TAG = "WEEK_FRAGMENT";
    private ViewPager2 weekViewPager;
    private WeekViewPagerAdapter weekViewPagerAdapter;

    private OnControlEvent onControlEvent;
    private static int COLUMN_WIDTH = AppMainActivity.getDisplayWidth() / 8;

    private OnPageChangeCallback onPageChangeCallback;

    public WeekFragment(Fragment fragment)
    {
        onControlEvent = (OnControlEvent) fragment;
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
        onPageChangeCallback = new OnPageChangeCallback();

        weekViewPager = (ViewPager2) view.findViewById(R.id.week_viewpager);
        weekViewPagerAdapter = new WeekViewPagerAdapter(this);
        weekViewPager.setAdapter(weekViewPagerAdapter);
        weekViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);
        weekViewPager.registerOnPageChangeCallback(onPageChangeCallback);

        super.onViewCreated(view, savedInstanceState);
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

    @Override
    public void onClicked(Date startDate, Date endDate)
    {

    }

    class OnPageChangeCallback extends ViewPager2.OnPageChangeCallback
    {
        private int currentPosition = EventTransactionFragment.FIRST_VIEW_POSITION;

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
        if (onPageChangeCallback.getCurrentPosition() != EventTransactionFragment.FIRST_VIEW_POSITION)
        {
            weekViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);
        }
    }

    public void onSelectedSchedules(int viewPosition, List<ScheduleDTO> schedules)
    {
        weekViewPagerAdapter.setData(viewPosition, schedules);
    }

    public void setMonth(Date date)
    {
        onControlEvent.setToolbarMonth(date);
    }

    public void requestSchedules(int position, Date startDate, Date endDate)
    {
        // 해당 페이지에 해당하는 날짜에 대한 데이터 불러오기
        onControlEvent.requestSchedules(this, position, startDate, endDate);
    }
}