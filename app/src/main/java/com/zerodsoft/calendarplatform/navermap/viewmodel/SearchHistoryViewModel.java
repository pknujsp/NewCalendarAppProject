package com.zerodsoft.calendarplatform.navermap.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.navermap.interfaces.SearchHistoryQuery;
import com.zerodsoft.calendarplatform.navermap.model.SearchLocationHistoryRepository;
import com.zerodsoft.calendarplatform.room.dto.SearchHistoryDTO;

import java.util.List;

public class SearchHistoryViewModel extends AndroidViewModel implements SearchHistoryQuery {
	private SearchLocationHistoryRepository repository;
	private MutableLiveData<SearchHistoryDTO> onAddedHistoryDTOMutableLiveData;
	private MutableLiveData<Integer> onRemovedHistoryDTOMutableLiveData;

	public SearchHistoryViewModel(@NonNull Application application) {
		super(application);
		this.repository = new SearchLocationHistoryRepository(application.getApplicationContext());
		onAddedHistoryDTOMutableLiveData = repository.getOnAddedHistoryDTOMutableLiveData();
		onRemovedHistoryDTOMutableLiveData = repository.getOnRemovedHistoryDTOMutableLiveData();
	}

	public LiveData<SearchHistoryDTO> getOnAddedHistoryDTOMutableLiveData() {
		return onAddedHistoryDTOMutableLiveData;
	}

	public LiveData<Integer> getOnRemovedHistoryDTOMutableLiveData() {
		return onRemovedHistoryDTOMutableLiveData;
	}

	@Override
	public void insert(Integer type, String value) {
		repository.insert(type, value);
	}

	@Override
	public void select(Integer type, DbQueryCallback<List<SearchHistoryDTO>> callback) {
		repository.select(type, callback);
	}

	@Override
	public void select(Integer type, String value, DbQueryCallback<SearchHistoryDTO> callback) {
		repository.select(type, value, callback);
	}

	@Override
	public void delete(int id) {
		repository.delete(id);
	}

	@Override
	public void delete(Integer type, String value, DbQueryCallback<Boolean> callback) {
		repository.delete(type, value, callback);
	}

	@Override
	public void deleteAll(Integer type, DbQueryCallback<Boolean> callback) {
		repository.deleteAll(type, callback);
	}

	@Override
	public void contains(Integer type, String value, DbQueryCallback<Boolean> callback) {
		repository.contains(type, value, callback);
	}
}
