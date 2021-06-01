package com.zerodsoft.scheduleweather.event.foods.repository;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

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

	public FoodCriteriaLocationInfoRepository(Application application) {
		dao = AppDb.getInstance(application.getApplicationContext()).foodCriteriaLocationInfoDAO();
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

	public MutableLiveData<FoodCriteriaLocationInfoDTO> getFoodCriteriaLocationInfo() {
		return foodCriteriaLocationInfo;
	}

	@Override
	public void selectByEventId(Long eventId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByEventId(eventId);
				callback.processResult(foodCriteriaLocationInfoDTO);
			}
		});
	}

	@Override
	public void selectByInstanceId(Long instanceId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByInstanceId(instanceId);
				callback.onReceiveResult(foodCriteriaLocationInfoDTO);
			}
		});
	}

	@Override
	public void insertByEventId(Long eventId, Integer usingType, Integer historyLocationId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.insertByEventId(eventId, usingType, historyLocationId);
				FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByEventId(eventId);
				callback.processResult(foodCriteriaLocationInfoDTO);
			}
		});
	}

	@Override
	public void insertByInstanceId(Long instanceId, Integer usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.insertByInstanceId(instanceId, usingType, historyLocationId);
				FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByInstanceId(instanceId);
				callback.onReceiveResult(foodCriteriaLocationInfoDTO);
			}
		});
	}

	@Override
	public void updateByEventId(Long eventId, Integer usingType, Integer historyLocationId, DbQueryCallback<FoodCriteriaLocationInfoDTO> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.updateByEventId(eventId, usingType, historyLocationId);
				FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByEventId(eventId);
				callback.processResult(foodCriteriaLocationInfoDTO);
			}
		});
	}

	@Override
	public void updateByInstanceId(Long instanceId, Integer usingType, Integer historyLocationId, CarrierMessagingService.ResultCallback<FoodCriteriaLocationInfoDTO> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.updateByInstanceId(instanceId, usingType, historyLocationId);
				FoodCriteriaLocationInfoDTO foodCriteriaLocationInfoDTO = dao.selectByInstanceId(instanceId);
				callback.onReceiveResult(foodCriteriaLocationInfoDTO);
			}
		});
	}

	@Override
	public void deleteByEventId(Long eventId, CarrierMessagingService.ResultCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.deleteByEventId(eventId);
				callback.onReceiveResult(true);
			}
		});
	}

	@Override
	public void deleteByInstanceId(Long instanceId, CarrierMessagingService.ResultCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.deleteByInstanceId(instanceId);
				callback.onReceiveResult(true);
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
}
