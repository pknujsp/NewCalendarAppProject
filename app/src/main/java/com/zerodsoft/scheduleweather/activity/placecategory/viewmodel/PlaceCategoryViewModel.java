package com.zerodsoft.scheduleweather.activity.placecategory.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.IPlaceCategory;
import com.zerodsoft.scheduleweather.activity.placecategory.model.PlaceCategoryData;
import com.zerodsoft.scheduleweather.activity.placecategory.repository.PlaceCategoryRepository;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.CustomPlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SelectedPlaceCategoryDTO;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlaceCategoryViewModel extends AndroidViewModel implements IPlaceCategory {
	private PlaceCategoryRepository repository;
	private MutableLiveData<List<PlaceCategoryDTO>> convertedSelectedCategoriesLiveData = new MutableLiveData<>();
	private MutableLiveData<PlaceCategoryDTO> onSelectedCategoryLiveData;
	private MutableLiveData<String> onUnSelectedCategoryLiveData;


	public PlaceCategoryViewModel(@NonNull Application application) {
		super(application);
		repository = new PlaceCategoryRepository(application.getApplicationContext());
		onSelectedCategoryLiveData = repository.getOnSelectedCategoryLiveData();
		onUnSelectedCategoryLiveData = repository.getOnUnSelectedCategoryLiveData();
	}

	public LiveData<PlaceCategoryDTO> getOnSelectedCategoryLiveData() {
		return onSelectedCategoryLiveData;
	}

	public LiveData<String> getOnUnSelectedCategoryLiveData() {
		return onUnSelectedCategoryLiveData;
	}

	@Override
	public void addCustom(String code, DbQueryCallback<CustomPlaceCategoryDTO> callback) {
		repository.addCustom(code, callback);
	}

	@Override
	public void getCustom(DbQueryCallback<List<CustomPlaceCategoryDTO>> callback) {
		repository.getCustom(callback);
	}


	@Override
	public void deleteCustom(String code, DbQueryCallback<Boolean> callback) {
		repository.deleteCustom(code, callback);
	}

	@Override
	public void deleteAllCustom(DbQueryCallback<Boolean> callback) {
		repository.deleteAllCustom(callback);
	}

	@Override
	public void addSelected(String code, DbQueryCallback<SelectedPlaceCategoryDTO> callback) {
		repository.addSelected(code, callback);
	}

	@Override
	public void deleteSelected(String code, @Nullable DbQueryCallback<Boolean> callback) {
		repository.deleteSelected(code, callback);
	}

	@Override
	public void deleteAllSelected(DbQueryCallback<Boolean> callback) {
		repository.deleteAllSelected(callback);
	}

	@Override
	public void getSelected(DbQueryCallback<List<SelectedPlaceCategoryDTO>> callback) {
		repository.getSelected(callback);
	}

	@Override
	public void getSettingsData(DbQueryCallback<PlaceCategoryData> callback) {
		repository.getSettingsData(callback);
	}

	@Override
	public void contains(String code, DbQueryCallback<Boolean> callback) {
		repository.contains(code, callback);
	}

	@Override
	public void selectConvertedSelected(DbQueryCallback<List<PlaceCategoryDTO>> callback) {
		repository.selectConvertedSelected(callback);
	}

	@Override
	public void addAllSelected(List<PlaceCategoryDTO> list, DbQueryCallback<List<SelectedPlaceCategoryDTO>> callback) {
		repository.addAllSelected(list, callback);
	}

	public MutableLiveData<List<PlaceCategoryDTO>> getConvertedSelectedCategoriesLiveData() {
		return convertedSelectedCategoriesLiveData;
	}
}
