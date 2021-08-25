package com.zerodsoft.scheduleweather.navermap.model;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.navermap.interfaces.SearchHistoryQuery;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.SearchHistoryDAO;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import java.util.List;

import lombok.SneakyThrows;

public class SearchLocationHistoryRepository implements SearchHistoryQuery {
	private SearchHistoryDAO dao;
	private MutableLiveData<SearchHistoryDTO> onAddedHistoryDTOMutableLiveData = new MutableLiveData<>();
	private MutableLiveData<Integer> onRemovedHistoryDTOMutableLiveData = new MutableLiveData<>();

	public SearchLocationHistoryRepository(Context context) {
		dao = AppDb.getInstance(context).searchHistoryDAO();
	}

	public MutableLiveData<SearchHistoryDTO> getOnAddedHistoryDTOMutableLiveData() {
		return onAddedHistoryDTOMutableLiveData;
	}

	public MutableLiveData<Integer> getOnRemovedHistoryDTOMutableLiveData() {
		return onRemovedHistoryDTOMutableLiveData;
	}

	@Override
	public void insert(Integer type, String value) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				dao.insert(type, value);
				SearchHistoryDTO searchHistoryDTO = dao.select(type, value);
				onAddedHistoryDTOMutableLiveData.postValue(searchHistoryDTO);
			}
		});
	}

	@Override
	public void select(Integer type, DbQueryCallback<List<SearchHistoryDTO>> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				List<SearchHistoryDTO> list = dao.select(type);
				callback.processResult(list);
			}
		});
	}

	@Override
	public void select(Integer type, String value, DbQueryCallback<SearchHistoryDTO> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				SearchHistoryDTO searchHistoryDTO = dao.select(type, value);
				callback.processResult(searchHistoryDTO);
			}
		});
	}

	@Override
	public void delete(int id) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				dao.delete(id);
				onRemovedHistoryDTOMutableLiveData.postValue(id);
			}
		});
	}

	@Override
	public void delete(Integer type, String value, DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				dao.delete(type, value);
				callback.processResult(true);
			}
		});
	}

	@Override
	public void deleteAll(Integer type, DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				dao.deleteAll(type);
				callback.processResult(true);
			}
		});
	}

	@Override
	public void contains(Integer type, String value, DbQueryCallback<Boolean> callback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				int result = dao.contains(type, value);
				callback.processResult(result == 1);
			}
		});
	}
}
