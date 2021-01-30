package com.zerodsoft.scheduleweather.event.common.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.google.android.gms.common.api.ResultCallback;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public interface ILocationDao
{
    public void getLocation(int calendarId, long eventId, CarrierMessagingService.ResultCallback<LocationDTO> resultCallback);

}