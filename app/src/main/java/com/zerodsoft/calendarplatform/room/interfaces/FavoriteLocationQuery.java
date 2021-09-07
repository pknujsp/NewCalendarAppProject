package com.zerodsoft.calendarplatform.room.interfaces;

import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FavoriteLocationQuery {
	void addNewFavoriteLocation(FavoriteLocationDTO favoriteLocationDTO, @Nullable DbQueryCallback<FavoriteLocationDTO> callback);

	void getFavoriteLocations(Integer type, DbQueryCallback<List<FavoriteLocationDTO>> callback);

	void getFavoriteLocation(Integer id, DbQueryCallback<FavoriteLocationDTO> callback);

	void delete(FavoriteLocationDTO favoriteLocationDTO, @Nullable DbQueryCallback<Boolean> callback);

	void deleteAll(Integer type, @Nullable DbQueryCallback<Boolean> callback);

	void deleteAll(@Nullable DbQueryCallback<Boolean> callback);

	void contains(String placeId, String latitude, String longitude, DbQueryCallback<FavoriteLocationDTO> callback);
}