package com.zerodsoft.scheduleweather.calendarfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zerodsoft.scheduleweather.AppMainActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.calendarview.viewmodel.CalendarViewModel;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.Date;
import java.util.List;


public class CalendarTransactionFragment extends Fragment implements OnControlCalendar
{
    // 달력 프래그먼트를 관리하는 프래그먼트
    public static final String TAG = "CalendarTransactionFragment";
    private static final int CALENDAR_CONTAINER_VIEW_ID = R.id.calendar_container_view;
    public static final int FIRST_VIEW_POSITION = Integer.MAX_VALUE / 2;
    public static int accountCategory;

    private CalendarViewModel calendarViewModel;

    private int viewPosition;
    private Fragment fragment;


    public CalendarTransactionFragment()
    {
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
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        // 마지막으로 사용된 달력의 종류 가져오기

        // 종류에 맞게 현재 날짜와 표시할 계정의 유형을 설정
        accountCategory = ScheduleDTO.ALL_CATEGORY;
        String calendarTag = DayFragment.TAG;

        replaceFragment(calendarTag);

        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.getSchedulesLiveData().observe(getViewLifecycleOwner(), new Observer<List<ScheduleDTO>>()
        {
            @Override
            public void onChanged(List<ScheduleDTO> scheduleList)
            {
                // 요청한 프래그먼트에 데이터 전달
                if (fragment instanceof MonthFragment)
                {
                    ((MonthFragment) fragment).onSelectedSchedules(viewPosition, scheduleList);
                } else if (fragment instanceof WeekFragment)
                {
                    ((WeekFragment) fragment).onSelectedSchedules(viewPosition, scheduleList);
                } else if (fragment instanceof DayFragment)
                {
                    ((DayFragment) fragment).onSelectedSchedules(viewPosition, scheduleList);
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    public void showScheduleInfo(int scheduleId)
    {
        Intent intent = new Intent(getActivity(), ScheduleInfoActivity.class);
        intent.putExtra("scheduleId", scheduleId);
        intent.putExtra("requestCode", ScheduleInfoActivity.REQUEST_SHOW_SCHEDULE);
        startActivityForResult(intent, ScheduleInfoActivity.REQUEST_SHOW_SCHEDULE);
    }

    public void replaceFragment(String fragmentTag)
    {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

        switch (fragmentTag)
        {
            case MonthFragment.TAG:
                fragment = new MonthFragment(CalendarTransactionFragment.this);
                fragmentTransaction.replace(CALENDAR_CONTAINER_VIEW_ID, (MonthFragment) fragment, MonthFragment.TAG);
                break;
            case WeekFragment.TAG:
                fragment = new WeekFragment(CalendarTransactionFragment.this);
                fragmentTransaction.replace(CALENDAR_CONTAINER_VIEW_ID, (WeekFragment) fragment, WeekFragment.TAG);
                break;
            case DayFragment.TAG:
                fragment = new DayFragment(CalendarTransactionFragment.this);
                fragmentTransaction.replace(CALENDAR_CONTAINER_VIEW_ID, (DayFragment) fragment, DayFragment.TAG);
                break;
        }
        fragmentTransaction.commit();
    }

    @Override
    public void showSchedule(int scheduleId)
    {

    }

    @Override
    public void requestSchedules(Fragment fragment, int viewPosition, Date startDate, Date endDate)
    {
        this.fragment = fragment;
        this.viewPosition = viewPosition;
        calendarViewModel.selectSchedules(startDate, endDate);
    }
}

interface OnControlCalendar
{
    void showSchedule(int scheduleId);

    void requestSchedules(Fragment fragment, int viewPosition, Date startDate, Date endDate);
}