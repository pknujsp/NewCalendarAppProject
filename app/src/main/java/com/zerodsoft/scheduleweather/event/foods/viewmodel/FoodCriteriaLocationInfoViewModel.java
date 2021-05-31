package com.zerodsoft.scheduleweather.event.foods.viewmodel;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodCriteriaLocationInfoQuery;
import com.zerodsoft.scheduleweather.event.foods.repository.FoodCriteriaLocationInfoRepository;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;

public class FoodCriteriaLocationInfoViewModel extends AndroidViewModel implements FoodCriteriaLocationInfoQuery {
	private FoodCriteriaLocationInfoRepository repository;

	public FoodCriteriaLocationInfoViewModel(@NonNull Application application) {
		super(application);
		repository = new FoodCriteriaLocationInfoRepository(application);
	}

	@Override
	public void selectByEventId(Long eventId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		repository.selectByEventId(eventId, callback);
	}

	@Override
	public void selectByInstanceId(Long instanceId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback) {
		repository.selectByInstanceId(instanceId, callback);
	}

	@Override
	public void insertByEventId(Long eventId, Integer usingType, Integer historyLocationId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		repository.insertByEventId(eventId, usingType, historyLocationId, callback);
	}

	@Override
	public void insertByInstanceId(Long instanceId, Integer usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback) {
		repository.insertByInstanceId(instanceId, usingType, historyLocationId, callback);
	}

	@Override
	public void updateByEventId(Long eventId, Integer usingType, Integer historyLocationId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		repository.updateByEventId(eventId, usingType, historyLocationId, callback);
	}

	@Override
	public void updateByInstanceId(Long instanceId, Integer usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback) {
		repository.updateByInstanceId(instanceId, usingType, historyLocationId, callback);
	}

	@Override
	public void deleteByEventId(Long eventId, CarrierMessagingService.ResultCallback<Boolean> callback) {
		repository.deleteByEventId(eventId, callback);
	}

	@Override
	public void deleteByInstanceId(Long instanceId, CarrierMessagingService.ResultCallback<Boolean> callback) {
		repository.deleteByInstanceId(instanceId, callback);
	}

	@Override
	public void contains(Long eventId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		repository.contains(eventId, callback);
	}
}
