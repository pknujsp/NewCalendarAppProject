package com.zerodsoft.scheduleweather.event.foods.repository;

import android.content.Context;
import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.foods.interfaces.FoodCriteriaLocationHistoryQuery;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.FoodCriteriaLocationSearchHistoryDAO;
import com.zerodsoft.scheduleweather.room.dto.FoodCriteriaLocationSearchHistoryDTO;

import java.util.List;

import lombok.SneakyThrows;

public class FoodCriteriaLocationHistoryRepository implements FoodCriteriaLocationHistoryQuery {
	private FoodCriteriaLocationSearchHistoryDAO dao;

	public FoodCriteriaLocationHistoryRepository(Context context) {
		dao = AppDb.getInstance(context).foodCriteriaLocationSearchHistoryDAO();
	}

	@Override
	public void selectByEventId(int calendarId, long eventId, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByEventId(calendarId, eventId);
				callback.processResult(list);
			}
		});
	}

	@Override
	public void selectByInstanceId(int calendarId, long instanceId, CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByInstanceId(calendarId, instanceId);
				callback.onReceiveResult(list);
			}
		});
	}

	@Override
	public void select(int id, DbQueryCallback<FoodCriteriaLocationSearchHistoryDTO> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				FoodCriteriaLocationSearchHistoryDTO result = dao.select(id);
				callback.processResult(result);
			}
		});
	}

	@Override
	public void selectAll(CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectAll();
				callback.onReceiveResult(list);
			}
		});
	}

	@Override
	public void insertByEventId(int calendarId, long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.insertByEventId(calendarId, eventId, placeName, addressName, roadAddressName, latitude, longitude, locationType);
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByEventId(calendarId, eventId);
				callback.processResult(list);
			}
		});
	}

	@Override
	public void insertByInstanceId(int calendarId, long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.insertByInstanceId(calendarId, instanceId, placeName, addressName, roadAddressName, latitude, longitude, locationType);
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByInstanceId(calendarId, instanceId);
				callback.onReceiveResult(list);
			}
		});
	}

	@Override
	public void updateByEventId(int calendarId, long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.updateByEventId(calendarId, eventId, placeName, addressName, roadAddressName, latitude, longitude, locationType);
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByEventId(calendarId, eventId);
				callback.onReceiveResult(list);
			}
		});
	}

	@Override
	public void updateByInstanceId(int calendarId, long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, CarrierMessagingService.ResultCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.updateByInstanceId(calendarId, instanceId, placeName, addressName, roadAddressName, latitude, longitude, locationType);
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByInstanceId(calendarId, instanceId);
				callback.onReceiveResult(list);
			}
		});
	}

	@Override
	public void update(int id, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, CarrierMessagingService.ResultCallback<FoodCriteriaLocationSearchHistoryDTO> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.update(id, placeName, addressName, roadAddressName, latitude, longitude, locationType);
				FoodCriteriaLocationSearchHistoryDTO result = dao.select(id);
				callback.onReceiveResult(result);
			}
		});
	}

	@Override
	public void deleteByEventId(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.deleteByEventId(calendarId, eventId);
				callback.onReceiveResult(true);
			}
		});
	}

	@Override
	public void deleteByInstanceId(int calendarId, long instanceId, CarrierMessagingService.ResultCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.deleteByInstanceId(calendarId, instanceId);
				callback.onReceiveResult(true);
			}
		});
	}

	@Override
	public void delete(int id, DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.delete(id);
				callback.processResult(true);
			}
		});
	}

	@Override
	public void deleteAll(CarrierMessagingService.ResultCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.deleteAll();
				callback.onReceiveResult(true);
			}
		});
	}

	@Override
	public void containsData(int id, DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				callback.processResult(dao.containsData(id) == 1);
			}
		});
	}
}
