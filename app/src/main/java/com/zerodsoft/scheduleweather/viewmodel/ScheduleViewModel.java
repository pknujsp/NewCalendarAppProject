package com.zerodsoft.scheduleweather.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.zerodsoft.scheduleweather.activity.ScheduleEditActivity;
import com.zerodsoft.scheduleweather.repositories.ScheduleRepository;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.Set;

public class ScheduleViewModel extends AndroidViewModel
{
    private ScheduleRepository scheduleRepository;

    private ScheduleDTO scheduleDTO;
    private PlaceDTO placeDTO;
    private AddressDTO addressDTO;

    private LiveData<ScheduleDTO> scheduleLiveData;
    private LiveData<PlaceDTO> placeLiveData;
    private LiveData<AddressDTO> addressLiveData;

    private MutableLiveData<ScheduleData> scheduleDataLiveData = new MutableLiveData<>();

    private int scheduleId;

    public ScheduleViewModel(@NonNull Application application)
    {
        super(application);
        scheduleRepository = new ScheduleRepository(application);
    }

    public ScheduleViewModel selectSchedule(int scheduleId)
    {
        scheduleRepository.selectSchedule(scheduleId);
        /*
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

         */
        return this;
    }

    public ScheduleViewModel selectScheduleData(int scheduleId)
    {
        scheduleRepository.selectScheduleData(scheduleId);
        return this;
    }

    public MutableLiveData<ScheduleData> getScheduleDataLiveData()
    {
        scheduleDataLiveData = scheduleRepository.getScheduleDataLiveData();
        return scheduleDataLiveData;
    }

    public ScheduleViewModel selectPlace(int scheduleId)
    {
        scheduleRepository.selectPlace(scheduleId);
        return this;
    }

    public ScheduleViewModel selectAddress(int scheduleId)
    {
        scheduleRepository.selectAddress(scheduleId);
        return this;
    }

    public LiveData<ScheduleDTO> getSchedule()
    {
        scheduleLiveData = scheduleRepository.getScheduleLiveData();
        return scheduleLiveData;
    }

    public LiveData<PlaceDTO> getPlace()
    {
        placeLiveData = scheduleRepository.getPlaceLiveData();
        return placeLiveData;
    }

    public LiveData<AddressDTO> getAddress()
    {
        addressLiveData = scheduleRepository.getAddressLiveData();
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
