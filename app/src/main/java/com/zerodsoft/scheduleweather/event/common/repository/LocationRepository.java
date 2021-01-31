package com.zerodsoft.scheduleweather.event.common.repository;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocationDao;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.LocationDAO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import lombok.SneakyThrows;

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
    public void getLocation(int calendarId, long eventId, CarrierMessagingService.ResultCallback<LocationDTO> resultCallback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                LocationDTO locationDTO = locationDAO.select(calendarId, eventId);
                locationLiveData.postValue(locationDTO == null ? new LocationDTO() : locationDTO);
                resultCallback.onReceiveResult(locationDTO == null ? new LocationDTO() : locationDTO);
            }
        });
    }

    @Override
    public void hasDetailLocation(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> resultCallback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                int result = locationDAO.hasLocation(calendarId, eventId);
                resultCallback.onReceiveResult(result == 1);
            }
        });
    }

    public MutableLiveData<LocationDTO> getLocationLiveData()
    {
        return locationLiveData;
    }
}
