package com.zerodsoft.scheduleweather.room.interfaces;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FavoriteLocationQuery {
	void insert(FavoriteLocationDTO favoriteLocationDTO, @Nullable DbQueryCallback<FavoriteLocationDTO> callback);

	void select(Integer type, DbQueryCallback<List<FavoriteLocationDTO>> callback);

	void select(Integer type, Integer id, DbQueryCallback<FavoriteLocationDTO> callback);

	void delete(FavoriteLocationDTO favoriteLocationDTO, @Nullable DbQueryCallback<Boolean> callback);

	void deleteAll(Integer type, @Nullable DbQueryCallback<Boolean> callback);

	void deleteAll(@Nullable DbQueryCallback<Boolean> callback);

	void contains(String placeId, String address, String latitude, String longitude, DbQueryCallback<FavoriteLocationDTO> callback);
}