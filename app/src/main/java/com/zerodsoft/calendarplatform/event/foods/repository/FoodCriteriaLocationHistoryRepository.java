package com.zerodsoft.calendarplatform.event.foods.repository;

import android.content.Context;

import com.zerodsoft.calendarplatform.activity.App;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.event.foods.interfaces.FoodCriteriaLocationHistoryQuery;
import com.zerodsoft.calendarplatform.room.AppDb;
import com.zerodsoft.calendarplatform.room.dao.FoodCriteriaLocationSearchHistoryDAO;
import com.zerodsoft.calendarplatform.room.dto.FoodCriteriaLocationSearchHistoryDTO;

import java.util.List;

public class FoodCriteriaLocationHistoryRepository implements FoodCriteriaLocationHistoryQuery {
	private FoodCriteriaLocationSearchHistoryDAO dao;

	public FoodCriteriaLocationHistoryRepository(Context context) {
		dao = AppDb.getInstance(context).foodCriteriaLocationSearchHistoryDAO();
	}

	@Override
	public void selectByEventId(long eventId, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByEventId(eventId);
				callback.processResult(list);
			}
		});
	}


	@Override
	public void select(int id, DbQueryCallback<FoodCriteriaLocationSearchHistoryDTO> callback) {
		App.executorService.execute(new Runnable() {
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
			@Override
			public void run() {
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectAll();
				callback.processResult(list);
			}
		});
	}

	@Override
	public void insertByEventId(long eventId, String placeName, String addressName, String latitude, String longitude, Integer locationType, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				dao.insertByEventId(eventId, placeName, addressName, latitude, longitude, locationType);
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByEventId(eventId);
				callback.processResult(list);
			}
		});
	}


	@Override
	public void updateByEventId(long eventId, String placeName, String addressName, String latitude, String longitude, Integer locationType, DbQueryCallback<List<FoodCriteriaLocationSearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				dao.updateByEventId(eventId, placeName, addressName, latitude, longitude, locationType);
				List<FoodCriteriaLocationSearchHistoryDTO> list = dao.selectByEventId(eventId);
				callback.processResult(list);
			}
		});
	}


	@Override
	public void update(int id, String placeName, String addressName, String latitude, String longitude, Integer locationType, DbQueryCallback<FoodCriteriaLocationSearchHistoryDTO> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				dao.update(id, placeName, addressName, latitude, longitude, locationType);
				FoodCriteriaLocationSearchHistoryDTO result = dao.select(id);
				callback.processResult(result);
			}
		});
	}

	@Override
	public void deleteByEventId(long eventId, DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				dao.deleteByEventId(eventId);
				callback.processResult(true);
			}
		});
	}

	@Override
	public void delete(int id, DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
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
			@Override
			public void run() {
				callback.processResult(dao.containsData(id) == 1);
			}
		});
	}
}
