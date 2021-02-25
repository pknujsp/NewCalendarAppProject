package com.zerodsoft.scheduleweather.activity.placecategory.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.zerodsoft.scheduleweather.activity.placecategory.repository.PlaceCategoryRepository;
import com.zerodsoft.scheduleweather.retrofit.PlaceCategory;

import java.util.List;

public class PlaceCategoryViewModel extends AndroidViewModel
{
    private PlaceCategoryRepository repository;
    private LiveData<List<PlaceCategory>> placeCategoryListLiveData;

    public PlaceCategoryViewModel(@NonNull Application application)
    {
        super(application);
        repository = new PlaceCategoryRepository(application.getApplicationContext());
        placeCategoryListLiveData = repository.getPlaceCategoryListLiveData();
    }

    public LiveData<List<PlaceCategory>> getPlaceCategoryListLiveData()
    {
        return placeCategoryListLiveData;
    }
}
