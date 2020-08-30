package com.zerodsoft.scheduleweather.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.zerodsoft.scheduleweather.Application;
import com.zerodsoft.scheduleweather.repositories.ScheduleRepository;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.thread.RepositoryCallback;
import com.zerodsoft.scheduleweather.thread.Result;

public class ScheduleViewModel extends AndroidViewModel
{
    private Context context;
    private ScheduleRepository scheduleRepository;

    private MutableLiveData<ScheduleDTO> scheduleLiveData;
    private MutableLiveData<PlaceDTO> placeLiveData;
    private MutableLiveData<AddressDTO> addressLiveData;

    private ScheduleDTO scheduleDTO;
    private PlaceDTO placeDTO;
    private AddressDTO addressDTO;

    private Observer<ScheduleDTO> observer;
    private int scheduleId;
    private OnDataListener onDataListener;

    public interface OnDataListener
    {
        void setViews(ScheduleDTO scheduleDTO);
    }

    public ScheduleViewModel(@NonNull Application application)
    {
        super(application);
        scheduleRepository = new ScheduleRepository(application);
        observer = new Observer<ScheduleDTO>()
        {
            @Override
            public void onChanged(ScheduleDTO scheduleDTO)
            {
                onDataListener.setViews(scheduleDTO);
            }
        }
    }

    public void setScheduleId(int scheduleId)
    {
        this.scheduleId = scheduleId;
    }

    public void selectSchedule()
    {
        scheduleRepository.selectSchedule(scheduleId, new RepositoryCallback<MutableLiveData<ScheduleDTO>>()
        {
            @Override
            public void onComplete(Result<MutableLiveData<ScheduleDTO>> result)
            {
                if (result instanceof Result.Success)
                {
                    scheduleLiveData = scheduleRepository.getScheduleLiveData();

                    if (!scheduleLiveData.getValue().isEmpty())
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
                    scheduleLiveData.observe();
                }
            }
        });
    }

    public MutableLiveData<ScheduleDTO> getSchedule()
    {
        scheduleLiveData = scheduleRepository.getScheduleLiveData();

        if (!scheduleLiveData.getValue().isEmpty())
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

    public MutableLiveData<PlaceDTO> getPlace()
    {
        return placeLiveData;
    }

    public MutableLiveData<AddressDTO> getAddress()
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
