package com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.categoryfragments;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategory;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import java.util.List;


public class PlaceItemDataSource extends PageKeyedDataSource<Integer, PlaceDocuments>
{

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, PlaceDocuments> callback)
    {
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, PlaceDocuments> callback)
    {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, PlaceDocuments> callback)
    {
    }


}
