package com.zerodsoft.calendarplatform.event.foods.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.calendarplatform.activity.App;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.event.foods.interfaces.CustomFoodMenuQuery;
import com.zerodsoft.calendarplatform.room.AppDb;
import com.zerodsoft.calendarplatform.room.dao.CustomFoodMenuDAO;
import com.zerodsoft.calendarplatform.room.dto.CustomFoodMenuDTO;

import java.util.List;

public class CustomFoodMenuRepository implements CustomFoodMenuQuery {
	private CustomFoodMenuDAO categoryDAO;
	private MutableLiveData<CustomFoodMenuDTO> onAddedCustomFoodMenuLiveData = new MutableLiveData<>();
	private MutableLiveData<Integer> onRemovedCustomFoodMenuLiveData = new MutableLiveData<>();


	public CustomFoodMenuRepository(Context context) {
		categoryDAO = AppDb.getInstance(context).customFoodCategoryDAO();
	}

	public MutableLiveData<CustomFoodMenuDTO> getOnAddedCustomFoodMenuLiveData() {
		return onAddedCustomFoodMenuLiveData;
	}

	public MutableLiveData<Integer> getOnRemovedCustomFoodMenuLiveData() {
		return onRemovedCustomFoodMenuLiveData;
	}

	@Override
	public void insert(String menuName, DbQueryCallback<CustomFoodMenuDTO> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				categoryDAO.insert(menuName);
				CustomFoodMenuDTO categoryDTO = categoryDAO.select(menuName);
				callback.processResult(categoryDTO);
				onAddedCustomFoodMenuLiveData.postValue(categoryDTO);
			}
		});
	}

	@Override
	public void select(DbQueryCallback<List<CustomFoodMenuDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				List<CustomFoodMenuDTO> list = categoryDAO.select();
				callback.processResult(list);
			}
		});
	}

	@Override
	public void delete(Integer id, DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				categoryDAO.delete(id);
				callback.processResult(true);
				onRemovedCustomFoodMenuLiveData.postValue(id);
			}
		});
	}

	@Override
	public void deleteAll(DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				categoryDAO.deleteAll();
				callback.processResult(true);
			}
		});
	}

	@Override
	public void containsMenu(String menuName, DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				int result = categoryDAO.containsMenu(menuName);
				callback.processResult(result == 1);
			}
		});
	}
}
