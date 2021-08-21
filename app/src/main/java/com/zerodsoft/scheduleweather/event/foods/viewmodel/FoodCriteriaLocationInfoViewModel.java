package com.zerodsoft.scheduleweather.event.foods.viewmodel;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodCriteriaLocationInfoQuery;
import com.zerodsoft.scheduleweather.event.foods.repository.FoodCriteriaLocationInfoRepository;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;

public class FoodCriteriaLocationInfoViewModel extends AndroidViewModel implements FoodCriteriaLocationInfoQuery {
	public static final String KEY = "FoodCriteriaLocationInfoViewModel";
	private FoodCriteriaLocationInfoRepository repository;
	private MutableLiveData<FoodCriteriaLocationInfoDTO> foodCriteriaLocationInfo;
	private MutableLiveData<FoodCriteriaLocationInfoDTO> onChangedCriteriaLocationLiveData;
	private MutableLiveData<FoodCriteriaLocationInfoDTO> onRefreshCriteriaLocationLiveData;


	public FoodCriteriaLocationInfoViewModel(@NonNull Application application) {
		super(application);
		repository = new FoodCriteriaLocationInfoRepository(application.getApplicationContext());
		foodCriteriaLocationInfo = repository.getFoodCriteriaLocationInfo();
		onChangedCriteriaLocationLiveData = repository.getOnChangedCriteriaLocationLiveData();
		onRefreshCriteriaLocationLiveData = repository.getOnRefreshCriteriaLocationLiveData();
	}

	public LiveData<FoodCriteriaLocationInfoDTO> getOnChangedCriteriaLocationLiveData() {
		return onChangedCriteriaLocationLiveData;
	}

	public LiveData<FoodCriteriaLocationInfoDTO> getFoodCriteriaLocationInfo() {
		return foodCriteriaLocationInfo;
	}

	public LiveData<FoodCriteriaLocationInfoDTO> getOnRefreshCriteriaLocationLiveData() {
		return onRefreshCriteriaLocationLiveData;
	}

	public void getInfoById(Long eventId) {
		repository.getInfoById(eventId);
	}

	@Override
	public void selectByEventId(Long eventId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		repository.selectByEventId(eventId, callback);
	}


	@Override
	public void insertByEventId(Long eventId, Integer usingType, Integer historyLocationId, @Nullable DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		repository.insertByEventId(eventId, usingType, historyLocationId, callback);
	}


	@Override
	public void updateByEventId(Long eventId, Integer usingType, Integer historyLocationId, @Nullable DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		repository.updateByEventId(eventId, usingType, historyLocationId, callback);
	}


	@Override
	public void deleteByEventId(Long eventId, @Nullable DbQueryCallback<Boolean> callback) {
		repository.deleteByEventId(eventId, callback);
	}


	@Override
	public void contains(Long eventId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		repository.contains(eventId, callback);
	}

	@Override
	public void refresh(Long eventId) {
		repository.refresh(eventId);
	}
}
