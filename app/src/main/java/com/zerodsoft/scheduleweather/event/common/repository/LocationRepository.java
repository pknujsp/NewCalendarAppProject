package com.zerodsoft.scheduleweather.event.common.repository;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.LocationDAO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class LocationRepository
{
    private MutableLiveData<LocationDTO> locationLiveData;
    private LocationDAO locationDAO;

    public LocationRepository(Application application)
    {
        locationDAO = AppDb.getInstance(application.getApplicationContext()).locationDAO();
        locationLiveData = new MutableLiveData<>();
    }

    public void select(int calendarId, int eventId, String accountName)
    {
        App.executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                locationLiveData.postValue(locationDAO.select(calendarId, eventId, accountName));
            }
        });
    }

    public MutableLiveData<LocationDTO> getLocationLiveData()
    {
        return locationLiveData;
    }
}
