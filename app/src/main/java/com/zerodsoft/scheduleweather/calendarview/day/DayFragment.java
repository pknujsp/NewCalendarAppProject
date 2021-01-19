package com.zerodsoft.scheduleweather.calendarview.day;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayFragment extends Fragment
{
    public static final String TAG = "DAY_FRAGMENT";

    private ViewPager2 dayViewPager;
    private DayViewPagerAdapter dayViewPagerAdapter;
    private IControlEvent iControlEvent;
    private IToolbar iToolbar;
    private OnPageChangeCallback onPageChangeCallback;
    private int currentPosition = EventTransactionFragment.FIRST_VIEW_POSITION;

    public DayFragment(IControlEvent iControlEvent, IToolbar iToolbar)
    {
        this.iControlEvent = iControlEvent;
        this.iToolbar = iToolbar;
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

        onPageChangeCallback = new OnPageChangeCallback(dayViewPagerAdapter.getCALENDAR());
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
        private Calendar calendar;
        private Calendar copiedCalendar;

        public OnPageChangeCallback(Calendar calendar)
        {
            this.calendar = calendar;
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
            currentPosition = position;

            copiedCalendar = (Calendar) calendar.clone();
            copiedCalendar.add(Calendar.DAY_OF_YEAR, currentPosition - EventTransactionFragment.FIRST_VIEW_POSITION);

            setMonth(copiedCalendar.getTime());
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
        iControlEvent.requestInstances(position, startDate, endDate, );
    }

    public void refreshView(Date startDate)
    {
        //시작 날짜의 해당 월로 달력 변경
        dayViewPagerAdapter = new DayViewPagerAdapter(this);
        //시작 날짜가 화면에 표시되도록 달력 날짜를 변경
        Calendar viewCalendar = dayViewPagerAdapter.getCALENDAR();
        //두 날짜 사이의 월수 차이
        int dayDifference = ClockUtil.calcDateDifference(ClockUtil.DAY, startDate, viewCalendar);
        dayViewPager.setAdapter(dayViewPagerAdapter);
        dayViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION + dayDifference, false);

        onPageChangeCallback = new OnPageChangeCallback(dayViewPagerAdapter.getCALENDAR());
        dayViewPager.registerOnPageChangeCallback(onPageChangeCallback);
    }

    public void setMonth(Date date)
    {
        iControlEvent.setToolbarMonth(date);
    }

    public void showSchedule(int scheduleId)
    {
        iControlEvent.showEventOnDayDialog(scheduleId, , );
    }

    public void goToToday()
    {
        if (currentPosition != EventTransactionFragment.FIRST_VIEW_POSITION)
        {
            dayViewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);
        }
    }
}
