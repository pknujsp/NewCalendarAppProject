package com.zerodsoft.scheduleweather.kakaomap.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

public class AddressItemDataSourceFactory extends DataSource.Factory<Integer, AddressResponseDocuments>
{
    private AddressItemDataSource dataSource;
    private MutableLiveData<AddressItemDataSource> liveData;
    private LocalApiPlaceParameter addressParameter;

    public AddressItemDataSourceFactory(LocalApiPlaceParameter addressParameter)
    {
        liveData = new MutableLiveData<>();
        this.addressParameter = addressParameter;
    }

    @NonNull
    @Override
    public DataSource<Integer, AddressResponseDocuments> create()
    {
        dataSource = new AddressItemDataSource(addressParameter);
        liveData.postValue(dataSource);
        return dataSource;
    }

    public MutableLiveData<AddressItemDataSource> getLiveData()
    {
        return liveData;
    }
}