package com.zerodsoft.scheduleweather.activity.placecategory.viewmodel;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.IPlaceCategory;
import com.zerodsoft.scheduleweather.activity.placecategory.model.PlaceCategoryData;
import com.zerodsoft.scheduleweather.activity.placecategory.repository.PlaceCategoryRepository;
import com.zerodsoft.scheduleweather.room.dto.CustomPlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SelectedPlaceCategoryDTO;

import java.util.List;

public class PlaceCategoryViewModel extends AndroidViewModel implements IPlaceCategory
{
    private PlaceCategoryRepository repository;

    public PlaceCategoryViewModel(@NonNull Application application)
    {
        super(application);
        repository = new PlaceCategoryRepository(application.getApplicationContext());
    }

    @Override
    public void insertCustom(String code, CarrierMessagingService.ResultCallback<CustomPlaceCategoryDTO> callback)
    {
        repository.insertCustom(code, callback);
    }

    @Override
    public void selectCustom(CarrierMessagingService.ResultCallback<List<CustomPlaceCategoryDTO>> callback)
    {
        repository.selectCustom(callback);
    }

    @Override
    public void updateCustom(String currentCode, String code, CarrierMessagingService.ResultCallback<CustomPlaceCategoryDTO> callback)
    {
        repository.updateCustom(currentCode, code, callback);
    }

    @Override
    public void deleteCustom(String code, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.deleteCustom(code, callback);
    }

    @Override
    public void deleteAllCustom(CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.deleteAllCustom(callback);
    }

    @Override
    public void insertSelected(String code, CarrierMessagingService.ResultCallback<SelectedPlaceCategoryDTO> callback)
    {
        repository.insertSelected(code, callback);
    }

    @Override
    public void deleteSelected(String code, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.deleteSelected(code, callback);
    }

    @Override
    public void deleteAllSelected(CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.deleteAllSelected(callback);
    }

    @Override
    public void selectSelected(CarrierMessagingService.ResultCallback<List<SelectedPlaceCategoryDTO>> callback)
    {
        repository.selectSelected(callback);
    }

    @Override
    public void getSettingsData(CarrierMessagingService.ResultCallback<PlaceCategoryData> callback)
    {
        repository.getSettingsData(callback);
    }

    @Override
    public void containsCode(String code, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.containsCode(code, callback);
    }

    @Override
    public void updateSelected(String currentCode, String code, CarrierMessagingService.ResultCallback<SelectedPlaceCategoryDTO> callback)
    {
        repository.updateSelected(currentCode, code, callback);
    }

    @Override
    public void selectConvertedSelected(CarrierMessagingService.ResultCallback<List<PlaceCategoryDTO>> callback)
    {
        repository.selectConvertedSelected(callback);
    }

    @Override
    public void insertAllSelected(List<PlaceCategoryDTO> list, CarrierMessagingService.ResultCallback<List<SelectedPlaceCategoryDTO>> callback)
    {
        repository.insertAllSelected(list, callback);
    }
}
