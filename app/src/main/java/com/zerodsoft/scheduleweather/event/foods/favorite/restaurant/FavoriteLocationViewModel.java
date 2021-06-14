package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FavoriteLocationViewModel extends AndroidViewModel implements FavoriteLocationQuery {
	private FavoriteLocationRepository restaurantRepository;
	private MutableLiveData<FavoriteLocationDTO> addedFavoriteLocationMutableLiveData;
	private MutableLiveData<FavoriteLocationDTO> removedFavoriteLocationMutableLiveData;

	public FavoriteLocationViewModel(@NonNull Application application) {
		super(application);
		restaurantRepository = new FavoriteLocationRepository(application.getApplicationContext());

		addedFavoriteLocationMutableLiveData = restaurantRepository.getAddedFavoriteLocationMutableLiveData();
		removedFavoriteLocationMutableLiveData = restaurantRepository.getRemovedFavoriteLocationMutableLiveData();
	}

	public LiveData<FavoriteLocationDTO> getAddedFavoriteLocationMutableLiveData() {
		return addedFavoriteLocationMutableLiveData;
	}

	public LiveData<FavoriteLocationDTO> getRemovedFavoriteLocationMutableLiveData() {
		return removedFavoriteLocationMutableLiveData;
	}

	@Override
	public void addNewFavoriteLocation(FavoriteLocationDTO favoriteLocationDTO, @Nullable DbQueryCallback<FavoriteLocationDTO> callback) {
		restaurantRepository.addNewFavoriteLocation(favoriteLocationDTO, callback);
	}

	@Override
	public void getFavoriteLocations(Integer type, DbQueryCallback<List<FavoriteLocationDTO>> callback) {
		restaurantRepository.getFavoriteLocations(type, callback);
	}

	@Override
	public void getFavoriteLocation(Integer id, DbQueryCallback<FavoriteLocationDTO> callback) {
		restaurantRepository.getFavoriteLocation(id, callback);
	}

	@Override
	public void delete(FavoriteLocationDTO favoriteLocationDTO, @Nullable DbQueryCallback<Boolean> callback) {
		restaurantRepository.delete(favoriteLocationDTO, callback);
	}

	@Override
	public void deleteAll(Integer type, @Nullable DbQueryCallback<Boolean> callback) {
		restaurantRepository.deleteAll(type, callback);
	}

	@Override
	public void deleteAll(@Nullable DbQueryCallback<Boolean> callback) {
		restaurantRepository.deleteAll(callback);
	}

	@Override
	public void contains(String placeId, String latitude, String longitude, DbQueryCallback<FavoriteLocationDTO> callback) {
		restaurantRepository.contains(placeId, latitude, longitude, callback);
	}
}
