package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import java.util.List;

public class FavoriteLocationViewModel extends AndroidViewModel implements FavoriteLocationQuery {
	private FavoriteLocationRepository restaurantRepository;
	private MutableLiveData<FavoriteLocationDTO> addedFavoriteLocationMutableLiveData;
	private MutableLiveData<Integer> removedFavoriteLocationMutableLiveData;

	public FavoriteLocationViewModel(@NonNull Application application) {
		super(application);
		restaurantRepository = new FavoriteLocationRepository(application.getApplicationContext());

		addedFavoriteLocationMutableLiveData = restaurantRepository.getAddedFavoriteLocationMutableLiveData();
		removedFavoriteLocationMutableLiveData = restaurantRepository.getRemovedFavoriteLocationMutableLiveData();
	}

	public LiveData<FavoriteLocationDTO> getAddedFavoriteLocationMutableLiveData() {
		return addedFavoriteLocationMutableLiveData;
	}

	public MutableLiveData<Integer> getRemovedFavoriteLocationMutableLiveData() {
		return removedFavoriteLocationMutableLiveData;
	}

	@Override
	public void addFavoriteLocation(FavoriteLocationDTO favoriteLocationDTO) {
		restaurantRepository.addFavoriteLocation(favoriteLocationDTO);
	}

	@Override
	public void insert(FavoriteLocationDTO favoriteLocationDTO, DbQueryCallback<FavoriteLocationDTO> callback) {
		restaurantRepository.insert(favoriteLocationDTO, callback);
	}

	@Override
	public void select(Integer type, DbQueryCallback<List<FavoriteLocationDTO>> callback) {
		restaurantRepository.select(type, callback);
	}

	@Override
	public void select(Integer type, Integer id, DbQueryCallback<FavoriteLocationDTO> callback) {
		restaurantRepository.select(type, id, callback);
	}

	@Override
	public void delete(Integer id, DbQueryCallback<Boolean> callback) {
		restaurantRepository.delete(id, callback);
	}

	@Override
	public void deleteAll(Integer type, DbQueryCallback<Boolean> callback) {
		restaurantRepository.deleteAll(type, callback);
	}

	@Override
	public void deleteAll(DbQueryCallback<Boolean> callback) {
		restaurantRepository.deleteAll(callback);
	}

	@Override
	public void contains(String placeId, String address, String latitude, String longitude, DbQueryCallback<FavoriteLocationDTO> callback) {
		restaurantRepository.contains(placeId, address, latitude, longitude, callback);
	}
}
