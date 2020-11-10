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

        if (places != null)
        {
            location = places.get(0).getPlaceName();
        } else if (addresses != null)
        {
            location = addresses.get(0).getAddressName();
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