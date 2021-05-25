package com.zerodsoft.scheduleweather.event.foods.viewmodel;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodCriteriaLocationHistoryQuery;
import com.zerodsoft.scheduleweather.event.foods.repository.FoodCriteriaLocationHistoryRepository;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;

import java.util.List;

public class FoodCriteriaLocationHistoryViewModel extends AndroidViewModel implements FoodCriteriaLocationHistoryQuery {
	private FoodCriteriaLocationHistoryRepository repository;

	public FoodCriteriaLocationHistoryViewModel(@NonNull Application application) {
		super(application);
		repository = new FoodCriteriaLocationHistoryRepository(application.getApplicationContext());
	}


	@Override
	public void selectByEventId(int calendarId, long eventId, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		repository.selectByEventId(calendarId, eventId, callback);
	}

	@Override
	public void selectByInstanceId(int calendarId, long instanceId, CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		repository.selectByInstanceId(calendarId, instanceId, callback);
	}

	@Override
	public void select(int id, DbQueryCallback<FoodCriteriaLocationSearchHistoryDTO> callback) {
		repository.select(id, callback);
	}

	@Override
	public void selectAll(CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		repository.selectAll(callback);
	}

	@Override
	public void insertByEventId(int calendarId, long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		repository.insertByEventId(calendarId, eventId, placeName, addressName, roadAddressName, latitude, longitude, locationType, callback);
	}

	@Override
	public void insertByInstanceId(int calendarId, long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		repository.insertByInstanceId(calendarId, instanceId, placeName, addressName, roadAddressName, latitude, longitude, locationType, callback);
	}

	@Override
	public void updateByEventId(int calendarId, long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		repository.updateByEventId(calendarId, eventId, placeName, addressName, roadAddressName, latitude, longitude, locationType, callback);
	}

	@Override
	public void updateByInstanceId(int calendarId, long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		repository.updateByInstanceId(calendarId, instanceId, placeName, addressName, roadAddressName, latitude, longitude, locationType, callback);
	}

	@Override
	public void update(int id, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, CarrierMessagingService.ResultCallback<FoodCriteriaLocationSearchHistoryDTO> callback) {
		repository.update(id, placeName, addressName, roadAddressName, latitude, longitude, locationType, callback);
	}

	@Override
	public void deleteByEventId(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> callback) {
		repository.deleteByEventId(calendarId, eventId, callback);
	}

	@Override
	public void deleteByInstanceId(int calendarId, long instanceId, CarrierMessagingService.ResultCallback<Boolean> callback) {
		repository.deleteByInstanceId(calendarId, instanceId, callback);
	}

	@Override
	public void delete(int id, DbQueryCallback<Boolean> callback) {
		repository.delete(id, callback);
	}

	@Override
	public void deleteAll(CarrierMessagingService.ResultCallback<Boolean> callback) {
		repository.deleteAll(callback);
	}

	@Override
	public void containsData(int id, DbQueryCallback<Boolean> callback) {
		repository.containsData(id, callback);
	}
}
