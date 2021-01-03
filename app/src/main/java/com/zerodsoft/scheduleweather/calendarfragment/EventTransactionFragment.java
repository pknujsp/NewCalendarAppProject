package com.zerodsoft.scheduleweather.calendarfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.viewmodel.CalendarViewModel;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.scheduleinfo.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Date;
import java.util.List;


public class EventTransactionFragment extends Fragment implements OnControlEvent
{
    // 달력 프래그먼트를 관리하는 프래그먼트
    public static final String TAG = "CalendarTransactionFragment";
    private final int CALENDAR_CONTAINER_VIEW_ID = R.id.calendar_container_view;
    public static final int FIRST_VIEW_POSITION = Integer.MAX_VALUE / 2;
    public static int accountCategory = ScheduleDTO.ALL_CATEGORY;

    private CalendarViewModel calendarViewModel;

    private int viewPosition;
    private Fragment fragment;

    public EventTransactionFragment()
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
        String calendarTag = MonthFragment.TAG;

        replaceFragment(calendarTag);

        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.getSchedulesLiveData().observe(getViewLifecycleOwner(), new Observer<List<ScheduleDTO>>()
        {
            @Override
            public void onChanged(@Nullable List<ScheduleDTO> scheduleList)
            {
                // 요청한 프래그먼트에 데이터 전달
                if (!scheduleList.isEmpty())
                {
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


    public void replaceFragment(String fragmentTag)
    {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

        switch (fragmentTag)
        {
            case MonthFragment.TAG:
                fragment = new MonthFragment(EventTransactionFragment.this);
                fragmentTransaction.replace(CALENDAR_CONTAINER_VIEW_ID, (MonthFragment) fragment, MonthFragment.TAG);
                break;
            case WeekFragment.TAG:
                fragment = new WeekFragment(EventTransactionFragment.this);
                fragmentTransaction.replace(CALENDAR_CONTAINER_VIEW_ID, (WeekFragment) fragment, WeekFragment.TAG);
                break;
            case DayFragment.TAG:
                fragment = new DayFragment(EventTransactionFragment.this);
                fragmentTransaction.replace(CALENDAR_CONTAINER_VIEW_ID, (DayFragment) fragment, DayFragment.TAG);
                break;
        }
        fragmentTransaction.commit();
    }

    @Override
    public void showSchedule(int scheduleId)
    {
        Intent intent = new Intent(getActivity(), ScheduleInfoActivity.class);
        intent.putExtra("scheduleId", scheduleId);
        startActivity(intent);
    }

    @Override
    public void setToolbarMonth(Date date)
    {
        ((TextView) getActivity().findViewById(R.id.calendar_month)).setText(ClockUtil.YEAR_MONTH_FORMAT.format(date));
    }

    @Override
    public void requestSchedules(Fragment fragment, int viewPosition, Date startDate, Date endDate)
    {
        this.fragment = fragment;
        this.viewPosition = viewPosition;
        calendarViewModel.selectSchedules(startDate, endDate);
    }

    public void refreshCalendar(Date startDate)
    {
        //일정이 추가/삭제되면 영향을 받은 일정의 시작날짜에 해당하는 달력의 위치로 이동한다.
        if (fragment instanceof MonthFragment)
        {
            ((MonthFragment) fragment).refreshView(startDate);
        } else if (fragment instanceof WeekFragment)
        {
            ((WeekFragment) fragment).refreshView(startDate);
        } else if (fragment instanceof DayFragment)
        {
            ((DayFragment) fragment).refreshView(startDate);
        }
    }

    public void goToToday()
    {
        if (fragment instanceof MonthFragment)
        {
            ((MonthFragment) fragment).goToToday();
        } else if (fragment instanceof WeekFragment)
        {
            ((WeekFragment) fragment).goToToday();
        } else if (fragment instanceof DayFragment)
        {
            ((DayFragment) fragment).goToToday();
        }
    }
}

