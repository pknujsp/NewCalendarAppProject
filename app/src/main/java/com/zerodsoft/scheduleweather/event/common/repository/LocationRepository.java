package com.zerodsoft.scheduleweather.event.common.repository;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocationDao;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.LocationDAO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class LocationRepository implements ILocationDao
{
    private MutableLiveData<LocationDTO> locationLiveData;
    private LocationDAO locationDAO;

    public LocationRepository(Application application)
    {
        locationDAO = AppDb.getInstance(application.getApplicationContext()).locationDAO();
        locationLiveData = new MutableLiveData<>();
    }

    @Override
    public void getLocation(int calendarId, long eventId)
    {
        App.executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                LocationDTO locationDTO = locationDAO.select(calendarId, eventId);
                locationLiveData.postValue(locationDTO == null ? new LocationDTO() : locationDTO);
            }
        });
    }

    public MutableLiveData<LocationDTO> getLocationLiveData()
    {
        return locationLiveData;
    }
}
