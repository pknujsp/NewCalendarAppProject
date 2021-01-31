package com.zerodsoft.scheduleweather.event.common.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public interface ILocation
{
    void showRequestLocDialog();

    void getLocation(CarrierMessagingService.ResultCallback<LocationDTO> resultCallback);

    boolean hasSimpleLocation();

    void hasDetailLocation(CarrierMessagingService.ResultCallback<Boolean> resultCallback);
}
