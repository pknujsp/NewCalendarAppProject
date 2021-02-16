package com.zerodsoft.scheduleweather.calendarview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.calendarview.day.DayFragment;
import com.zerodsoft.scheduleweather.calendarview.instancedialog.InstanceListOnDayFragment;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.month.MonthFragment;
import com.zerodsoft.scheduleweather.calendarview.week.WeekFragment;
import com.zerodsoft.scheduleweather.event.EventActivity;

import java.util.List;


public class EventTransactionFragment extends Fragment implements IControlEvent, OnEventItemClickListener
{
    // 달력 프래그먼트를 관리하는 프래그먼트
    public static final String TAG = "CalendarTransactionFragment";
    public static final int FIRST_VIEW_POSITION = Integer.MAX_VALUE / 2;

    private int viewPosition;
    private CalendarViewModel calendarViewModel;
    private Fragment fragment;
    private IToolbar iToolbar;
    private IConnectedCalendars iConnectedCalendars;

    public EventTransactionFragment(Activity activity)
    {
        this.iToolbar = (IToolbar) activity;
        this.iConnectedCalendars = (IConnectedCalendars) activity;
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

        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.init(getContext());
        // 마지막으로 사용된 달력의 종류 가져오기
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
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();

        switch (fragmentTag)
        {
            case MonthFragment.TAG:
                fragment = new MonthFragment(this, iToolbar);
                fragmentTransaction.replace(R.id.calendar_container_layout, (MonthFragment) fragment, MonthFragment.TAG);
                break;
            case WeekFragment.TAG:
                fragment = new WeekFragment(this, iToolbar);
                fragmentTransaction.replace(R.id.calendar_container_layout, (WeekFragment) fragment, WeekFragment.TAG);
                break;
            case DayFragment.TAG:
                fragment = new DayFragment(this, iToolbar);
                fragmentTransaction.replace(R.id.calendar_container_layout, (DayFragment) fragment, DayFragment.TAG);
                break;
        }
        fragmentTransaction.commit();
    }


    @Override
    public void getInstances(int viewPosition, long start, long end, EventCallback<List<CalendarInstance>> callback)
    {
        // 선택된 캘린더 목록
        calendarViewModel.getInstanceList(iConnectedCalendars.getConnectedCalendars(), start, end, callback);
    }


    public void refreshCalendar()
    {
        //일정이 추가/삭제되면 영향을 받은 일정의 시작날짜에 해당하는 달력의 위치로 이동한다.
        if (fragment instanceof MonthFragment)
        {
            ((MonthFragment) fragment).refreshView();
        } else if (fragment instanceof WeekFragment)
        {
            ((WeekFragment) fragment).refreshView();
        } else if (fragment instanceof DayFragment)
        {
            ((DayFragment) fragment).refreshView();
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

    @Override
    public void onClicked(long viewBegin, long viewEnd)
    {
        // 이벤트 리스트 프래그먼트 다이얼로그 표시
        InstanceListOnDayFragment fragment = new InstanceListOnDayFragment(iConnectedCalendars, this);
        Bundle bundle = new Bundle();
        bundle.putLong("begin", viewBegin);
        bundle.putLong("end", viewEnd);

        fragment.setArguments(bundle);
        fragment.show(getParentFragmentManager(), InstanceListOnDayFragment.TAG);
    }

    @Override
    public void onClicked(int calendarId, long instanceId, long eventId, long viewBegin, long viewEnd)
    {
        // 이벤트 정보 액티비티로 전환
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra("calendarId", calendarId);
        intent.putExtra("instanceId", instanceId);
        intent.putExtra("eventId", eventId);
        intent.putExtra("begin", viewBegin);
        intent.putExtra("end", viewEnd);

        startActivity(intent);
    }
}
