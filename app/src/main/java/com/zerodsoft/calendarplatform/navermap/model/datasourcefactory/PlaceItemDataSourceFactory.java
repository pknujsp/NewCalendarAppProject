package com.zerodsoft.calendarplatform.navermap.model.datasourcefactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.zerodsoft.calendarplatform.navermap.model.datasource.PlaceItemDataSource;
import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

public class PlaceItemDataSourceFactory extends DataSource.Factory<Integer, PlaceDocuments>
{
    private PlaceItemDataSource dataSource;
    private MutableLiveData<PlaceItemDataSource> liveData;
    private LocalApiPlaceParameter placeParameter;

    public PlaceItemDataSourceFactory(LocalApiPlaceParameter placeParameter)
    {
        liveData = new MutableLiveData<>();
        this.placeParameter = placeParameter;
    }

    @NonNull
    @Override
    public DataSource<Integer, PlaceDocuments> create()
    {
        dataSource = new PlaceItemDataSource(placeParameter);
        liveData.postValue(dataSource);
        return dataSource;
    }


    public MutableLiveData<PlaceItemDataSource> getLiveData()
    {
        return liveData;
    }
}
