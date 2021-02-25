package com.zerodsoft.scheduleweather.activity.placecategory.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.retrofit.PlaceCategory;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.PlaceCategoryDAO;

import java.util.List;

public class PlaceCategoryRepository
{
    private final PlaceCategoryDAO placeCategoryDAO;
    private LiveData<List<PlaceCategory>> placeCategoryListLiveData;

    public PlaceCategoryRepository(Context context)
    {
        placeCategoryDAO = AppDb.getInstance(context).placeCategoryDAO();
        placeCategoryListLiveData = placeCategoryDAO.select();

    }

    public LiveData<List<PlaceCategory>> getPlaceCategoryListLiveData()
    {
        return placeCategoryListLiveData;


    }
}
