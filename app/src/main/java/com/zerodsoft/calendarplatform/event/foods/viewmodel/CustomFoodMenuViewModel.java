package com.zerodsoft.calendarplatform.event.foods.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.event.foods.interfaces.CustomFoodMenuQuery;
import com.zerodsoft.calendarplatform.event.foods.repository.CustomFoodMenuRepository;
import com.zerodsoft.calendarplatform.room.dto.CustomFoodMenuDTO;

import java.util.List;

public class CustomFoodMenuViewModel extends AndroidViewModel implements CustomFoodMenuQuery {
	private CustomFoodMenuRepository repository;
	private MutableLiveData<CustomFoodMenuDTO> onAddedCustomFoodMenuLiveData;
	private MutableLiveData<Integer> onRemovedCustomFoodMenuLiveData;

	public CustomFoodMenuViewModel(@NonNull Application application) {
		super(application);
		repository = new CustomFoodMenuRepository(application.getApplicationContext());
		onAddedCustomFoodMenuLiveData = repository.getOnAddedCustomFoodMenuLiveData();
		onRemovedCustomFoodMenuLiveData = repository.getOnRemovedCustomFoodMenuLiveData();
	}

	public LiveData<CustomFoodMenuDTO> getOnAddedCustomFoodMenuLiveData() {
		return onAddedCustomFoodMenuLiveData;
	}

	public LiveData<Integer> getOnRemovedCustomFoodMenuLiveData() {
		return onRemovedCustomFoodMenuLiveData;
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
	public void delete(Integer id, DbQueryCallback<Boolean> callback) {
		repository.delete(id, callback);
	}

	@Override
	public void deleteAll(DbQueryCallback<Boolean> callback) {
		repository.deleteAll(callback);
	}

	@Override
	public void containsMenu(String menuName, DbQueryCallback<Boolean> callback) {
		repository.containsMenu(menuName, callback);
	}
}
