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
import com.zerodsoft.scheduleweather.calendarfragment.EventsInfoFragment.EventsInfoFragment;
import com.zerodsoft.scheduleweather.calendarview.month.MonthViewPagerAdapter;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthFragment extends Fragment implements OnEventItemClickListener
{
    public static final String TAG = "MonthFragment";

    private OnControlEvent onControlEvent;
    private ViewPager2 viewPager;
    private MonthViewPagerAdapter viewPagerAdapter;
    private OnPageChangeCallback onPageChangeCallback;
    private int currentPosition = EventTransactionFragment.FIRST_VIEW_POSITION;


    public MonthFragment(Fragment fragment)
    {
        onControlEvent = (OnControlEvent) fragment;
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
        viewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);

        onPageChangeCallback = new OnPageChangeCallback(viewPagerAdapter.getCalendar());
        viewPager.registerOnPageChangeCallback(onPageChangeCallback);

        super.onViewCreated(view, savedInstanceState);
    }

    public void onSelectedSchedules(int viewPosition, List<ScheduleDTO> schedules)
    {
        viewPagerAdapter.setData(viewPosition, schedules);
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

    public void refreshView(Date startDate)
    {
        //시작 날짜의 해당 월로 달력 변경
        viewPagerAdapter = new MonthViewPagerAdapter(this);
        //시작 날짜가 화면에 표시되도록 달력 날짜를 변경
        Calendar viewCalendar = viewPagerAdapter.getCalendar();
        //두 날짜 사이의 월수 차이
        int monthDifference = ClockUtil.calcDateDifference(ClockUtil.MONTH, startDate, viewCalendar);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION + monthDifference, false);

        onPageChangeCallback = new OnPageChangeCallback(viewPagerAdapter.getCalendar());
        viewPager.registerOnPageChangeCallback(onPageChangeCallback);
    }

    public void goToToday()
    {
        if (currentPosition != EventTransactionFragment.FIRST_VIEW_POSITION)
        {
            viewPager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);
        }
    }

    @Override
    public void onClicked(Date startDate, Date endDate)
    {
        EventsInfoFragment eventsInfoFragment = new EventsInfoFragment(startDate, endDate);
        eventsInfoFragment.show(getActivity().getSupportFragmentManager(), EventsInfoFragment.TAG);
    }

    public void showSchedule(int scheduleId)
    {
        onControlEvent.showSchedule(scheduleId);
    }


    class OnPageChangeCallback extends ViewPager2.OnPageChangeCallback
    {
        private final Calendar calendar;
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
            copiedCalendar.add(Calendar.MONTH, currentPosition - EventTransactionFragment.FIRST_VIEW_POSITION);

            //error point-------------------------------------------------------------------------
            setMonth(copiedCalendar.getTime());
            super.onPageSelected(position);
        }
    }
}

