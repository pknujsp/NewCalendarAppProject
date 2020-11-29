package com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.categoryfragments;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

public class PlaceItemDataSourceFactory extends DataSource.Factory<Integer, PlaceDocuments>
{
    @NonNull
    @Override
    public DataSource<Integer, PlaceDocuments> create()
    {
        return new PlaceItemDataSource();
    }
}
