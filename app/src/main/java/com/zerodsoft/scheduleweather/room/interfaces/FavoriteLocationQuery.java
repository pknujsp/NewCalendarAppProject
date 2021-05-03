package com.zerodsoft.scheduleweather.room.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import java.util.List;

public interface FavoriteLocationQuery
{
    void insert(FavoriteLocationDTO favoriteLocationDTO, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback);

    void select(Integer type, CarrierMessagingService.ResultCallback<List<FavoriteLocationDTO>> callback);

    void select(Integer type, Integer id, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback);

    void delete(Integer id, CarrierMessagingService.ResultCallback<Boolean> callback);

    void deleteAll(Integer type, CarrierMessagingService.ResultCallback<Boolean> callback);

    void contains(Integer type, String placeId, String address, String latitude, String longitude, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback);
}