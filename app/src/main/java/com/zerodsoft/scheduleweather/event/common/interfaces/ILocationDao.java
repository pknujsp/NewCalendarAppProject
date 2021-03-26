package com.zerodsoft.scheduleweather.event.common.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public interface ILocationDao
{
    public void getLocation(int calendarId, long eventId, CarrierMessagingService.ResultCallback<LocationDTO> resultCallback);

    public void hasDetailLocation(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> resultCallback);

    public void addLocation(LocationDTO location, CarrierMessagingService.ResultCallback<Boolean> resultCallback);

    public void removeLocation(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> resultCallback);

    public void modifyLocation(LocationDTO location, CarrierMessagingService.ResultCallback<Boolean> resultCallback);

    public void getAddressItem(LocalApiPlaceParameter parameter, CarrierMessagingService.ResultCallback<DataWrapper<AddressResponseDocuments>> callback);

    public void getPlaceItem(LocalApiPlaceParameter parameter, String placeId, CarrierMessagingService.ResultCallback<DataWrapper<PlaceDocuments>> callback);
}