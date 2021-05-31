package com.zerodsoft.scheduleweather.event.foods.viewmodel;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.foods.interfaces.CustomFoodMenuQuery;
import com.zerodsoft.scheduleweather.event.foods.repository.CustomFoodMenuRepository;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;

import java.util.List;

public class CustomFoodMenuViewModel extends AndroidViewModel implements CustomFoodMenuQuery {
	private CustomFoodMenuRepository repository;

	public CustomFoodMenuViewModel(@NonNull Application application) {
		super(application);
		repository = new CustomFoodMenuRepository(application.getApplicationContext());
	}

	@Override
	public void insert(String menuName, DbQueryCallback<CustomFoodMenuDTO> callback) {
		repository.insert(menuName, callback);
	}

	@Override
	public void select(DbQueryCallback<List<CustomFoodMenuDTO>> callback) {
		repository.select(callback);
	}

	@Override
	public void update(String previousMenuName, String newMenuName, CarrierMessagingService.ResultCallback<CustomFoodMenuDTO> callback) {
		repository.update(previousMenuName, newMenuName, callback);
	}

	@Override
	public void delete(String menuName, CarrierMessagingService.ResultCallback<Boolean> callback) {
		repository.delete(menuName, callback);
	}

	@Override
	public void deleteAll(CarrierMessagingService.ResultCallback<Boolean> callback) {
		repository.deleteAll(callback);
	}

	@Override
	public void containsMenu(String menuName, CarrierMessagingService.ResultCallback<Boolean> callback) {
		repository.containsMenu(menuName, callback);
	}
}
