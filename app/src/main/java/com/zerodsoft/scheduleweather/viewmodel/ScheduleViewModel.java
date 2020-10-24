package com.zerodsoft.scheduleweather.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.zerodsoft.scheduleweather.activity.ScheduleEditActivity;
import com.zerodsoft.scheduleweather.repositories.ScheduleRepository;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

public class ScheduleViewModel extends AndroidViewModel
{
    private ScheduleRepository scheduleRepository;

    private ScheduleDTO scheduleDTO;
    private PlaceDTO placeDTO;
    private AddressDTO addressDTO;

    private LiveData<ScheduleDTO> scheduleLiveData;
    private LiveData<PlaceDTO> placeLiveData;
    private LiveData<AddressDTO> addressLiveData;

    private int scheduleId;

    public ScheduleViewModel(@NonNull Application application)
    {
        super(application);
        scheduleRepository = new ScheduleRepository(application);
    }

    public ScheduleViewModel selectSchedule(int scheduleId)
    {
        this.scheduleId = scheduleId;

        scheduleRepository.selectSchedule(scheduleId);
        scheduleRepository.selectAddress(scheduleId);
        scheduleRepository.selectPlace(scheduleId);

        scheduleLiveData = scheduleRepository.getScheduleLiveData();
        addressLiveData = scheduleRepository.getAddressLiveData();
        placeLiveData = scheduleRepository.getPlaceLiveData();

        scheduleLiveData = Transformations.map(scheduleLiveData, new Function<ScheduleDTO, ScheduleDTO>()
        {
            @Override
            public ScheduleDTO apply(ScheduleDTO input)
            {
                if (input != null)
                {
                    addressLiveData = scheduleRepository.getAddressLiveData();
                    placeLiveData = scheduleRepository.getPlaceLiveData();
                } else
                {
                    input = new ScheduleDTO();
                }
                return input;
            }
        });
        return this;
    }

    public LiveData<ScheduleDTO> getSchedule()
    {
        return scheduleLiveData;
    }

    public LiveData<PlaceDTO> getPlace()
    {
        return placeLiveData;
    }

    public LiveData<AddressDTO> getAddress()
    {
        return addressLiveData;
    }

    public void deleteSchedule()
    {
        scheduleRepository.deleteSchedule(ScheduleEditActivity.scheduleId);
    }

    public void updateSchedule()
    {
        scheduleRepository.updateSchedule(scheduleDTO, placeDTO, addressDTO);
    }

    public void insertSchedule()
    {
        scheduleRepository.insertSchedule(scheduleDTO, placeDTO, addressDTO);
    }

    public void setAddressDTO(AddressDTO addressDTO)
    {
        this.addressDTO = addressDTO;
    }

    public void setPlaceDTO(PlaceDTO placeDTO)
    {
        this.placeDTO = placeDTO;
    }

    public void setScheduleDTO(ScheduleDTO scheduleDTO)
    {
        this.scheduleDTO = scheduleDTO;
    }
}
