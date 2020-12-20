package com.zerodsoft.scheduleweather.scheduleinfo;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.databinding.FragmentScheduleInfoBinding;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.utility.ScheduleAlarm;

import java.util.List;

public class ScheduleInfoFragment extends Fragment
{
    public static final String TAG = "ScheduleInfoFragment";
    private FragmentScheduleInfoBinding binding;

    private ScheduleDTO schedule;
    private List<AddressDTO> addresses;
    private List<PlaceDTO> places;

    public ScheduleInfoFragment(ScheduleDTO schedule, List<AddressDTO> addresses, List<PlaceDTO> places)
    {
        setSchedule(schedule, addresses, places);
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

    public ScheduleInfoFragment setSchedule(ScheduleDTO schedule, List<AddressDTO> addresses, List<PlaceDTO> places)
    {
        this.schedule = schedule;
        this.places = places;
        this.addresses = addresses;
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
            time = ClockUtil.dateFormat3.format(schedule.getStartDate());
        } else
        {
            time = ClockUtil.dateFormat2.format(schedule.getStartDate()) + " -> " + ClockUtil.dateFormat2.format(schedule.getEndDate());
        }
        binding.scheduleTime.setText(time);

        // 내용
        binding.scheduleContent.setText(schedule.getContent());

        // 위치
        String location = null;

        if (!places.isEmpty())
        {
            location = places.get(0).getPlaceName();
        } else if (!addresses.isEmpty())
        {
            location = addresses.get(0).getAddressName();
        } else
        {
            location = "";
        }

        binding.scheduleLocation.setText(location);

        // 알람
        ScheduleAlarm.setDAY(schedule.getNotiDay());
        ScheduleAlarm.setHOUR(schedule.getNotiHour());
        ScheduleAlarm.setMINUTE(schedule.getNotiMinute());
        if (!ScheduleAlarm.isEmpty())
        {
            binding.scheduleAlarm.setText(ScheduleAlarm.getResultText(getContext()));
        } else
        {
            binding.scheduleAlarm.setText("");
            binding.scheduleAlarm.setHint(R.string.noti_time_not_selected);
        }
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