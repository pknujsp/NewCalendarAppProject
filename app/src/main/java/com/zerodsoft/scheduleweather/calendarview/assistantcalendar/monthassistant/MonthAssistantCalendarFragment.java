package com.zerodsoft.scheduleweather.calendarview.assistantcalendar.monthassistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.callback.EventCallback;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.month.MonthViewPagerAdapter;
import com.zerodsoft.scheduleweather.databinding.FragmentMonthAssistantBinding;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MonthAssistantCalendarFragment extends Fragment implements IControlEvent
{
    public static final String TAG = "MonthAssistantCalendarFragment";
    private FragmentMonthAssistantBinding binding;
    private MonthAssistantCalendarListAdapter adapter;
    private final IConnectedCalendars iConnectedCalendars;
    private CalendarViewModel calendarViewModel;

    public MonthAssistantCalendarFragment(Activity activity)
    {
        this.iConnectedCalendars = (IConnectedCalendars) activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentMonthAssistantBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.init(getContext());
        adapter = new MonthAssistantCalendarListAdapter(this);

        binding.monthAssistantCalendarViewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.monthAssistantCalendarViewpager.setAdapter(adapter);
        binding.monthAssistantCalendarViewpager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);

        binding.currentMonthButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        binding.previousMonthButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                binding.monthAssistantCalendarViewpager.setCurrentItem(binding.monthAssistantCalendarViewpager.getCurrentItem() - 1, true);
            }
        });

        binding.nextMonthButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                binding.monthAssistantCalendarViewpager.setCurrentItem(binding.monthAssistantCalendarViewpager.getCurrentItem() + 1, true);
            }
        });
    }

    @Override
    public void getInstances(int viewPosition, long start, long end, EventCallback<List<CalendarInstance>> callback)
    {
        calendarViewModel.getInstanceList(iConnectedCalendars.getConnectedCalendars(), start, end, callback);
    }

    /**
     * 현재 년월 텍스트를 클릭하면 보조 캘린더의 날짜를 현재 month로 설정하고 표시
     **/
    public void setCurrentMonth(Date date)
    {
        // 개월 수 차이 계산
        int monthDifference = ClockUtil.calcMonthDifference(date, adapter.getAsOfDate());
        binding.monthAssistantCalendarViewpager.setCurrentItem(monthDifference, false);
    }
}
