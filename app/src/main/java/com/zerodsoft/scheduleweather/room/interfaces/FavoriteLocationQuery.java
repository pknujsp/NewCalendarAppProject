package com.zerodsoft.scheduleweather.room.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import java.util.List;

public interface FavoriteLocationQuery
{
    void insert(String locationId, String locationName, String latitude, String longitude, Integer type, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback);

    void select(Integer type, CarrierMessagingService.ResultCallback<List<FavoriteLocationDTO>> callback);

    void select(String locationName, Integer type, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback);

    void delete(String locationName, Integer type, CarrierMessagingService.ResultCallback<Boolean> callback);

    void deleteAll(Integer type, CarrierMessagingService.ResultCallback<Boolean> callback);

    void contains(Integer type, String locationName, String locationId, CarrierMessagingService.ResultCallback<Boolean> callback);
}
