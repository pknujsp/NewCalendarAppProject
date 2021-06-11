package com.zerodsoft.scheduleweather.room.interfaces;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import java.util.List;

public interface FavoriteLocationQuery {
	void insert(FavoriteLocationDTO favoriteLocationDTO, DbQueryCallback<FavoriteLocationDTO> callback);

	void addFavoriteLocation(FavoriteLocationDTO favoriteLocationDTO);

	void select(Integer type, DbQueryCallback<List<FavoriteLocationDTO>> callback);

	void select(Integer type, Integer id, DbQueryCallback<FavoriteLocationDTO> callback);

	void delete(Integer id, DbQueryCallback<Boolean> callback);

	void deleteAll(Integer type, DbQueryCallback<Boolean> callback);

	void deleteAll(DbQueryCallback<Boolean> callback);

	void contains(String placeId, String address, String latitude, String longitude, DbQueryCallback<FavoriteLocationDTO> callback);
}