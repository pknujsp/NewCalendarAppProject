package com.zerodsoft.scheduleweather.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.zerodsoft.scheduleweather.repositories.ScheduleRepository;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

public class ScheduleViewModel extends AndroidViewModel
{
    private Context context;
    private ScheduleRepository scheduleRepository;

    private LiveData<ScheduleDTO> scheduleLiveData;
    private LiveData<PlaceDTO> placeLiveData;
    private LiveData<AddressDTO> addressLiveData;

    private ScheduleDTO scheduleDTO;
    private PlaceDTO placeDTO;
    private AddressDTO addressDTO;

    private int scheduleId;

    public ScheduleViewModel(@NonNull Application application)
    {
        super(application);
        scheduleRepository = new ScheduleRepository(application);
    }

    public void setScheduleId(int scheduleId)
    {
        this.scheduleId = scheduleId;
    }

    public void selectSchedule()
    {
        scheduleRepository.selectSchedule(scheduleId);
    }

    public LiveData<ScheduleDTO> getSchedule()
    {
        scheduleLiveData = scheduleRepository.getScheduleLiveData();

        if (scheduleLiveData.getValue() != null)
        {
            addressLiveData = null;
            placeLiveData = null;

            if (scheduleLiveData.getValue().getAddress() != -1)
            {
                addressLiveData = scheduleRepository.getAddressLiveData();
            } else if (scheduleLiveData.getValue().getPlace() != -1)
            {
                placeLiveData = scheduleRepository.getPlaceLiveData();
            }
        }
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
        scheduleRepository.deleteSchedule();
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
