package com.zerodsoft.calendarplatform.event.common.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.calendarplatform.room.dto.LocationDTO;

public interface ILocation
{
    void showRequestLocDialog();

    void getLocation(CarrierMessagingService.ResultCallback<LocationDTO> resultCallback);

    boolean hasSimpleLocation();

    void hasDetailLocation(CarrierMessagingService.ResultCallback<Boolean> resultCallback);
}