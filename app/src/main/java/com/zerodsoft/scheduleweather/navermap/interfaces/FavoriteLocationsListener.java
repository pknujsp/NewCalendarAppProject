package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import java.io.Serializable;
import java.util.List;

public interface FavoriteLocationsListener extends Serializable {
	void createFavoriteLocationsPoiItems(List<FavoriteLocationDTO> favoriteLocationList);

	void createFavoriteLocationsPoiItem(FavoriteLocationDTO favoriteLocationDTO, double latitude, double longitude);

	void addFavoriteLocationsPoiItem(FavoriteLocationDTO favoriteLocationDTO);

	void removeFavoriteLocationsPoiItem(int id);
}
