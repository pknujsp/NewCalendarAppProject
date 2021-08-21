package com.zerodsoft.scheduleweather.event.foods.repository;

import android.content.Context;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodCriteriaLocationInfoQuery;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.FoodCriteriaLocationInfoDAO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationInfoDTO;

import lombok.SneakyThrows;

public class FoodCriteriaLocationInfoRepository implements FoodCriteriaLocationInfoQuery {
	private FoodCriteriaLocationInfoDAO dao;
	private MutableLiveData<FoodCriteriaLocationInfoDTO> foodCriteriaLocationInfo = new MutableLiveData(new FoodCriteriaLocationInfoDTO());
	private MutableLiveData<FoodCriteriaLocationInfoDTO> onChangedCriteriaLocationLiveData = new MutableLiveData<>();
	private MutableLiveData<FoodCriteriaLocationInfoDTO> onRefreshCriteriaLocationLiveData = new MutableLiveData<>();


	public FoodCriteriaLocationInfoRepository(Context context) {
		dao = AppDb.getInstance(context).foodCriteriaLocationInfoDAO();
	}

	public void getInfoById(Long eventId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByEventId(eventId);
				foodCriteriaLocationInfo.postValue(foodCriteriaLocationInfoDTO);
			}
		}).start();
	}

	public MutableLiveData<FoodCriteriaLocationInfoDTO> getOnChangedCriteriaLocationLiveData() {
		return onChangedCriteriaLocationLiveData;
	}

	public MutableLiveData<FoodCriteriaLocationInfoDTO> getFoodCriteriaLocationInfo() {
		return foodCriteriaLocationInfo;
	}

	public MutableLiveData<FoodCriteriaLocationInfoDTO> getOnRefreshCriteriaLocationLiveData() {
		return onRefreshCriteriaLocationLiveData;
	}

	@Override
	public void selectByEventId(Long eventId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByEventId(eventId);
				callback.processResult(foodCriteriaLocationInfoDTO);
			}
		});
	}


	@Override
	public void insertByEventId(Long eventId, Integer usingType, Integer historyLocationId, @Nullable DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				dao.insertByEventId(eventId, usingType, historyLocationId);
				FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByEventId(eventId);
				if (callback != null) {
					callback.processResult(foodCriteriaLocationInfoDTO);
				}
			}
		});
	}


	@Override
	public void updateByEventId(Long eventId, Integer usingType, Integer historyLocationId,
	                            @Nullable DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				dao.updateByEventId(eventId, usingType, historyLocationId);
				FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByEventId(eventId);
				if (callback != null) {
					callback.processResult(foodCriteriaLocationInfoDTO);
				}
				onChangedCriteriaLocationLiveData.postValue(foodCriteriaLocationInfoDTO);
			}
		});
	}


	@Override
	public void deleteByEventId(Long eventId, @Nullable DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				dao.deleteByEventId(eventId);
				if (callback != null) {
					callback.processResult(true);
				}
			}
		});
	}


	@Override
	public void contains(Long eventId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				callback.processResult(dao.contains(eventId));
			}
		}).start();
	}

	@Override
	public void refresh(Long eventId) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByEventId(eventId);
				onRefreshCriteriaLocationLiveData.postValue(foodCriteriaLocationInfoDTO);
			}
		});
	}
}
