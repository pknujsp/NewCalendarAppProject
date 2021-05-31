package com.zerodsoft.scheduleweather.event.foods.repository;

import android.content.Context;

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
	public void selectByEventId(long eventId, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByEventId(eventId);
				callback.processResult(list);
			}
		});
	}

	@Override
	public void selectByInstanceId(long instanceId, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByInstanceId(instanceId);
				callback.processResult(list);
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
	public void selectAll(DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectAll();
				callback.processResult(list);
			}
		});
	}

	@Override
	public void insertByEventId(long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.insertByEventId(eventId, placeName, addressName, roadAddressName, latitude, longitude, locationType);
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByEventId(eventId);
				callback.processResult(list);
			}
		});
	}

	@Override
	public void insertByInstanceId(long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.insertByInstanceId(instanceId, placeName, addressName, roadAddressName, latitude, longitude, locationType);
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByInstanceId(instanceId);
				callback.processResult(list);
			}
		});
	}

	@Override
	public void updateByEventId(long eventId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.updateByEventId(eventId, placeName, addressName, roadAddressName, latitude, longitude, locationType);
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByEventId(eventId);
				callback.processResult(list);
			}
		});
	}

	@Override
	public void updateByInstanceId(long instanceId, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.updateByInstanceId(instanceId, placeName, addressName, roadAddressName, latitude, longitude, locationType);
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByInstanceId(instanceId);
				callback.processResult(list);
			}
		});
	}

	@Override
	public void update(int id, String placeName, String addressName, String roadAddressName, String latitude, String longitude, Integer locationType, DbQueryCallback<FoodCriteriaLocationSearchHistoryDTO> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.update(id, placeName, addressName, roadAddressName, latitude, longitude, locationType);
				FoodCriteriaLocationSearchHistoryDTO result = dao.select(id);
				callback.processResult(result);
			}
		});
	}

	@Override
	public void deleteByEventId(long eventId, DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.deleteByEventId(eventId);
				callback.processResult(true);
			}
		});
	}

	@Override
	public void deleteByInstanceId(long instanceId, DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.deleteByInstanceId(instanceId);
				callback.processResult(true);
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
	public void deleteAll(DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				dao.deleteAll();
				callback.processResult(true);
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
