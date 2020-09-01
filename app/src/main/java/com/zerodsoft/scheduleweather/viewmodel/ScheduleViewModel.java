package com.zerodsoft.scheduleweather.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.zerodsoft.scheduleweather.activity.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.repositories.ScheduleRepository;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.thread.RepositoryCallback;
import com.zerodsoft.scheduleweather.thread.Result;

public class ScheduleViewModel extends AndroidViewModel
{
    private ScheduleRepository scheduleRepository;

    private ScheduleDTO scheduleDTO;
    private PlaceDTO placeDTO;
    private AddressDTO addressDTO;

    private LiveData<ScheduleDTO> scheduleLiveData;
    private LiveData<PlaceDTO> placeLiveData;
    private LiveData<AddressDTO> addressLiveData;

    public ScheduleViewModel(@NonNull Application application)
    {
        super(application);
        scheduleRepository = new ScheduleRepository(application, ScheduleInfoActivity.scheduleId);
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
                    if (input.getAddress() != ScheduleDTO.NOT_LOCATION)
                    {
                        addressLiveData = scheduleRepository.getAddressLiveData();
                    } else if (input.getPlace() != ScheduleDTO.NOT_LOCATION)
                    {
                        placeLiveData = scheduleRepository.getPlaceLiveData();
                    }
                } else
                {
                    input = new ScheduleDTO();
                }
                return input;
            }
        });
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
        scheduleRepository.deleteSchedule(ScheduleInfoActivity.scheduleId);
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
