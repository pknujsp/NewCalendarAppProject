package com.zerodsoft.scheduleweather.event.common.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zerodsoft.scheduleweather.event.common.repository.LocationRepository;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class LocationViewModel extends AndroidViewModel
{
    private MutableLiveData<LocationDTO> locationLiveData;
    private LocationRepository locationRepository;

    public LocationViewModel(@NonNull Application application)
    {
        super(application);
        locationRepository = new LocationRepository(application);
        locationLiveData = locationRepository.getLocationLiveData();
    }

    public void select(int calendarId, int eventId, String accountName)
    {
        locationRepository.select(calendarId, eventId, accountName);
    }

    public MutableLiveData<LocationDTO> getLocationLiveData()
    {
        return locationLiveData;
    }
}
