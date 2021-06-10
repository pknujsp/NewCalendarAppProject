package com.zerodsoft.scheduleweather.navermap.model.datasourcefactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.zerodsoft.scheduleweather.common.interfaces.OnProgressBarListener;
import com.zerodsoft.scheduleweather.navermap.model.datasource.AddressItemDataSource;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;

public class AddressItemDataSourceFactory extends DataSource.Factory<Integer, AddressResponseDocuments>
{
    private AddressItemDataSource dataSource;
    private MutableLiveData<AddressItemDataSource> liveData;
    private LocalApiPlaceParameter addressParameter;

    public AddressItemDataSourceFactory(LocalApiPlaceParameter addressParameter )
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