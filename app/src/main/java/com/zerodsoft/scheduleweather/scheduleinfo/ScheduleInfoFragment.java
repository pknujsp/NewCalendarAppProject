package com.zerodsoft.scheduleweather.scheduleinfo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentScheduleInfoBinding;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.Clock;
import com.zerodsoft.scheduleweather.utility.ScheduleAlarm;

public class ScheduleInfoFragment extends Fragment
{
    public static final String TAG = "ScheduleInfoFragment";
    private FragmentScheduleInfoBinding binding;

    private ScheduleDTO schedule;
    private PlaceDTO place;
    private AddressDTO address;

    public ScheduleInfoFragment(ScheduleDTO schedule, PlaceDTO place, AddressDTO address)
    {
        setSchedule(schedule, place, address);
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
        binding = FragmentScheduleInfoBinding.bind(inflater.inflate(R.layout.fragment_schedule_info, container, false));
        return binding.getRoot();
    }

    public ScheduleInfoFragment setSchedule(ScheduleDTO schedule, PlaceDTO place, AddressDTO address)
    {
        this.schedule = schedule;
        this.place = place;
        this.address = address;
        return this;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        // 제목
        binding.scheduleSubject.setText(schedule.getSubject());

        // 시간
        String time = null;

        if (schedule.getStartDate().equals(schedule.getEndDate()))
        {
            time = Clock.dateFormat3.format(schedule.getStartDate());
        } else
        {
            time = Clock.dateFormat2.format(schedule.getStartDate()) + " -> " + Clock.dateFormat2.format(schedule.getEndDate());
        }
        binding.scheduleTime.setText(time);

        // 내용
        binding.scheduleContent.setText(schedule.getContent());

        // 위치
        String location = null;

        if (place != null)
        {
            location = place.getPlaceName();
        } else if (address != null)
        {
            location = address.getAddressName();
        } else
        {
            location = "";
        }

        binding.scheduleLocation.setText(location);

        // 알람
        binding.scheduleAlarm.setText(ScheduleAlarm.getResultText());

        // 계정
        if (schedule.getCategory() == ScheduleDTO.GOOGLE_CATEGORY)
        {
            binding.scheduleAccount.setText("구글");
        } else
        {
            binding.scheduleAccount.setText("로컬");
        }
    }
}