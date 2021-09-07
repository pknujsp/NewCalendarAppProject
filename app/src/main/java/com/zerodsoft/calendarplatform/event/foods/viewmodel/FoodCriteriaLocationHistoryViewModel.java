package com.zerodsoft.calendarplatform.event.foods.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.event.foods.interfaces.FoodCriteriaLocationHistoryQuery;
import com.zerodsoft.calendarplatform.event.foods.repository.FoodCriteriaLocationHistoryRepository;
import com.zerodsoft.calendarplatform.room.dto.FoodCriteriaLocationSearchHistoryDTO;

import java.util.List;

public class FoodCriteriaLocationHistoryViewModel extends AndroidViewModel implements FoodCriteriaLocationHistoryQuery {
	public static final String KEY = "FoodCriteriaLocationHistoryViewModel";
	private FoodCriteriaLocationHistoryRepository repository;

	public FoodCriteriaLocationHistoryViewModel(@NonNull Application application) {
		super(application);
		repository = new FoodCriteriaLocationHistoryRepository(application.getApplicationContext());
	}


	@Override
	public void selectByEventId(long eventId, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		repository.selectByEventId(eventId, callback);
	}

	@Override
	public void select(int id, DbQueryCallback<FoodCriteriaLocationSearchHistoryDTO> callback) {
		repository.select(id, callback);
	}

	@Override
	public void selectAll(DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		repository.selectAll(callback);
	}

	@Override
	public void insertByEventId(long eventId, String placeName, String addressName, String latitude, String longitude, Integer locationType, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		repository.insertByEventId(eventId, placeName, addressName, latitude, longitude, locationType, callback);
	}

	@Override
	public void updateByEventId(long eventId, String placeName, String addressName, String latitude, String longitude, Integer locationType, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		repository.updateByEventId(eventId, placeName, addressName, latitude, longitude, locationType, callback);
	}


	@Override
	public void update(int id, String placeName, String addressName, String latitude, String longitude, Integer locationType, DbQueryCallback<FoodCriteriaLocationSearchHistoryDTO> callback) {
		repository.update(id, placeName, addressName, latitude, longitude, locationType, callback);
	}

	@Override
	public void deleteByEventId(long eventId, DbQueryCallback<Boolean> callback) {
		repository.deleteByEventId(eventId, callback);
	}

	@Override
	public void delete(int id, DbQueryCallback<Boolean> callback) {
		repository.delete(id, callback);
	}

	@Override
	public void deleteAll(DbQueryCallback<Boolean> callback) {
		repository.deleteAll(callback);
	}

	@Override
	public void containsData(int id, DbQueryCallback<Boolean> callback) {
		repository.containsData(id, callback);
	}
}
