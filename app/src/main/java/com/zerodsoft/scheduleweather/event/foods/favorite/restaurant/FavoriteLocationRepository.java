package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.FavoriteLocationDAO;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import java.util.List;

import lombok.SneakyThrows;

class FavoriteLocationRepository implements FavoriteLocationQuery {
	private final FavoriteLocationDAO dao;
	private MutableLiveData<FavoriteLocationDTO> addedFavoriteLocationMutableLiveData = new MutableLiveData<>();
	private MutableLiveData<Integer> removedFavoriteLocationMutableLiveData = new MutableLiveData<>();

	public FavoriteLocationRepository(Context context) {
		dao = AppDb.getInstance(context).favoriteRestaurantDAO();
	}

	public MutableLiveData<FavoriteLocationDTO> getAddedFavoriteLocationMutableLiveData() {
		return addedFavoriteLocationMutableLiveData;
	}

	public MutableLiveData<Integer> getRemovedFavoriteLocationMutableLiveData() {
		return removedFavoriteLocationMutableLiveData;
	}

	@Override
	public void insert(FavoriteLocationDTO favoriteLocationDTO, DbQueryCallback<FavoriteLocationDTO> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				long id = dao.insert(favoriteLocationDTO);
				int type = favoriteLocationDTO.getType();
				FavoriteLocationDTO favoriteLocationDTO = dao.select(type, (int) id);
				callback.processResult(favoriteLocationDTO);
				addedFavoriteLocationMutableLiveData.postValue(favoriteLocationDTO);
			}
		}).start();
	}

	@Override
	public void addFavoriteLocation(FavoriteLocationDTO favoriteLocationDTO) {

	}

	@Override
	public void select(Integer type, DbQueryCallback<List<FavoriteLocationDTO>> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<FavoriteLocationDTO> list = dao.select(type);
				callback.processResult(list);
			}
		}).start();
	}

	@Override
	public void select(Integer type, Integer id, DbQueryCallback<FavoriteLocationDTO> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				FavoriteLocationDTO favoriteLocationDTO = dao.select(type, id);
				callback.processResult(favoriteLocationDTO);
			}
		}).start();
	}

	@Override
	public void delete(Integer id, DbQueryCallback<Boolean> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				dao.delete(id);
				callback.processResult(true);
				removedFavoriteLocationMutableLiveData.postValue(id);
			}
		}).start();
	}

	@Override
	public void deleteAll(Integer type, DbQueryCallback<Boolean> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				dao.deleteAll(type);
				callback.processResult(true);
			}
		}).start();
	}

	@Override
	public void deleteAll(DbQueryCallback<Boolean> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				dao.deleteAll();
				callback.processResult(true);
			}
		}).start();
	}

	@Override
	public void contains(String placeId, String address, String latitude, String longitude, DbQueryCallback<FavoriteLocationDTO> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				FavoriteLocationDTO favoriteLocationDTO = dao.contains(placeId, address, latitude, longitude);
				callback.processResult(favoriteLocationDTO);
			}
		}).start();
	}
}

