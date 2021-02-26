package com.zerodsoft.scheduleweather.activity.placecategory.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
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
    private MutableLiveData<List<SelectedPlaceCategoryDTO>> selectedPlaceCategoryListLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CustomPlaceCategoryDTO>> customPlaceCategoryListLiveData = new MutableLiveData<>();
    private MutableLiveData<PlaceCategoryData> placeCategoryDataLiveData = new MutableLiveData<>();

    public PlaceCategoryViewModel(@NonNull Application application)
    {
        super(application);
        repository = new PlaceCategoryRepository(application.getApplicationContext());
    }

    public MutableLiveData<List<SelectedPlaceCategoryDTO>> getSelectedPlaceCategoryListLiveData()
    {
        return selectedPlaceCategoryListLiveData;
    }

    public MutableLiveData<List<CustomPlaceCategoryDTO>> getCustomPlaceCategoryListLiveData()
    {
        return customPlaceCategoryListLiveData;
    }

    public MutableLiveData<PlaceCategoryData> getPlaceCategoryDataLiveData()
    {
        return placeCategoryDataLiveData;
    }

    @Override
    public void insertCustom(String code)
    {
        repository.insertCustom(code);
    }

    @Override
    public void selectCustom()
    {
        repository.selectCustom();
    }

    @Override
    public void updateCustom(int id, String code)
    {
        repository.updateCustom(id, code);
    }

    @Override
    public void deleteCustom(int id)
    {
        repository.deleteCustom(id);
    }

    @Override
    public void deleteAllCustom()
    {
        repository.deleteAllCustom();
    }

    @Override
    public void insertSelected(String code)
    {
        repository.insertSelected(code);
    }

    @Override
    public void deleteSelected(String code)
    {
        repository.deleteSelected(code);
    }

    @Override
    public void deleteAllSelected()
    {
        repository.deleteAllSelected();
    }

    @Override
    public void selectSelected()
    {
        repository.selectSelected();
    }

    @Override
    public void getSettingsData()
    {
        repository.getSettingsData();
    }
}
