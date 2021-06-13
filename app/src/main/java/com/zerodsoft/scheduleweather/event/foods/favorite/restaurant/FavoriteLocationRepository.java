package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.FavoriteLocationDAO;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import lombok.SneakyThrows;

class FavoriteLocationRepository implements FavoriteLocationQuery {
	private final FavoriteLocationDAO dao;
	private MutableLiveData<FavoriteLocationDTO> addedFavoriteLocationMutableLiveData = new MutableLiveData<>();
	private MutableLiveData<FavoriteLocationDTO> removedFavoriteLocationMutableLiveData = new MutableLiveData<>();

	public FavoriteLocationRepository(Context context) {
		dao = AppDb.getInstance(context).favoriteRestaurantDAO();
	}

	public MutableLiveData<FavoriteLocationDTO> getAddedFavoriteLocationMutableLiveData() {
		return addedFavoriteLocationMutableLiveData;
	}

	public MutableLiveData<FavoriteLocationDTO> getRemovedFavoriteLocationMutableLiveData() {
		return removedFavoriteLocationMutableLiveData;
	}

	@Override
	public void insert(FavoriteLocationDTO favoriteLocationDTO, @Nullable DbQueryCallback<FavoriteLocationDTO> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				long id = dao.insert(favoriteLocationDTO);
				int type = favoriteLocationDTO.getType();
				FavoriteLocationDTO favoriteLocationDTO = dao.select(type, (int) id);
				addedFavoriteLocationMutableLiveData.postValue(favoriteLocationDTO);
				if (callback != null) {
					callback.processResult(favoriteLocationDTO);
				}
			}
		}).start();
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
	public void delete(FavoriteLocationDTO favoriteLocationDTO, @Nullable DbQueryCallback<Boolean> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				dao.delete(favoriteLocationDTO.getId());
				if (callback != null) {
					callback.processResult(true);
				}
				removedFavoriteLocationMutableLiveData.postValue(favoriteLocationDTO);
			}
		}).start();
	}

	@Override
	public void deleteAll(Integer type, @Nullable DbQueryCallback<Boolean> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				dao.deleteAll(type);
				if (callback != null) {
					callback.processResult(true);
				}
			}
		}).start();
	}

	@Override
	public void deleteAll(@Nullable DbQueryCallback<Boolean> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				dao.deleteAll();
				if (callback != null) {
					callback.processResult(true);
				}
			}
		}).start();
	}

	@Override
	public void contains(String placeId, String address, String latitude, String longitude, DbQueryCallback<FavoriteLocationDTO> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				FavoriteLocationDTO favoriteLocationDTO = dao.contains(placeId, address, latitude, longitude);
				callback.processResult(favoriteLocationDTO);
			}
		}).start();
	}
}

