package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.app.Application;
import android.content.Context;
import android.service.carrier.CarrierMessagingService;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.FavoriteLocationDAO;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import java.util.List;

import lombok.SneakyThrows;

class FavoriteLocationRepository implements FavoriteLocationQuery {
	private final FavoriteLocationDAO dao;
	private MutableLiveData<FavoriteLocationDTO> favoriteLocationDTOMutableLiveData = new MutableLiveData<>(new FavoriteLocationDTO());

	public FavoriteLocationRepository(Context context) {
		dao = AppDb.getInstance(context).favoriteRestaurantDAO();
	}

	public MutableLiveData<FavoriteLocationDTO> getFavoriteLocationDTOMutableLiveData() {
		return favoriteLocationDTOMutableLiveData;
	}

	@Override
	public void insert(FavoriteLocationDTO favoriteLocationDTO, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				long id = dao.insert(favoriteLocationDTO);
				int type = favoriteLocationDTO.getType();
				FavoriteLocationDTO favoriteLocationDTO = dao.select(type, (int) id);
				callback.onReceiveResult(favoriteLocationDTO);
			}
		}).start();
	}

	@Override
	public void addFavoriteLocation(FavoriteLocationDTO favoriteLocationDTO) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				long id = dao.insert(favoriteLocationDTO);
				int type = favoriteLocationDTO.getType();
				FavoriteLocationDTO addedFavoriteLocation = dao.select(type, (int) id);
				favoriteLocationDTOMutableLiveData.postValue(addedFavoriteLocation);
			}
		}).start();
	}

	@Override
	public void select(Integer type, CarrierMessagingService.ResultCallback<List<FavoriteLocationDTO>> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				List<FavoriteLocationDTO> list = dao.select(type);
				callback.onReceiveResult(list);
			}
		}).start();
	}

	@Override
	public void select(Integer type, Integer id, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				FavoriteLocationDTO favoriteLocationDTO = dao.select(type, id);
				callback.onReceiveResult(favoriteLocationDTO);
			}
		}).start();
	}

	@Override
	public void delete(Integer id, CarrierMessagingService.ResultCallback<Boolean> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.delete(id);
				callback.onReceiveResult(true);
			}
		}).start();
	}

	@Override
	public void deleteAll(Integer type, CarrierMessagingService.ResultCallback<Boolean> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.deleteAll(type);
				callback.onReceiveResult(true);
			}
		}).start();
	}

	@Override
	public void deleteAll(CarrierMessagingService.ResultCallback<Boolean> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.deleteAll();
				callback.onReceiveResult(true);
			}
		}).start();
	}

	@Override
	public void contains(String placeId, String address, String latitude, String longitude, CarrierMessagingService.ResultCallback<FavoriteLocationDTO> callback) {
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				FavoriteLocationDTO favoriteLocationDTO = dao.contains(placeId, address, latitude, longitude);
				callback.onReceiveResult(favoriteLocationDTO);
			}
		}).start();
	}
}

