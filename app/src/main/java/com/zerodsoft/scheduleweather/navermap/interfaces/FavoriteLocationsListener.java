package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import java.util.List;

public interface FavoriteLocationsListener
{
    void createFavoriteLocationsPoiItems(List<FavoriteLocationDTO> favoriteLocationList);

    void createFavoriteLocationsPoiItem(FavoriteLocationDTO favoriteLocationDTO, double latitude, double longitude);
}
