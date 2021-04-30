package com.zerodsoft.scheduleweather.navermap.model.datasourcefactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.navermap.model.datasource.PlaceItemDataSource;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

public class PlaceItemDataSourceFactory extends DataSource.Factory<Integer, PlaceDocuments>
{
    private PlaceItemDataSource dataSource;
    private MutableLiveData<PlaceItemDataSource> liveData;
    private LocalApiPlaceParameter placeParameter;
    private OnProgressBarListener onProgressBarListener;

    public PlaceItemDataSourceFactory(LocalApiPlaceParameter placeParameter, OnProgressBarListener onProgressBarListener)
    {
        liveData = new MutableLiveData<>();
        this.placeParameter = placeParameter;
        this.onProgressBarListener = onProgressBarListener;
    }

    @NonNull
    @Override
    public DataSource<Integer, PlaceDocuments> create()
    {
        dataSource = new PlaceItemDataSource(placeParameter, onProgressBarListener);
        liveData.postValue(dataSource);
        return dataSource;
    }


    public MutableLiveData<PlaceItemDataSource> getLiveData()
    {
        return liveData;
    }
}
