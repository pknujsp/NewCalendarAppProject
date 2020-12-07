package com.zerodsoft.scheduleweather.kakaomap.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.zerodsoft.scheduleweather.kakaomap.model.AddressItemDataSource;
import com.zerodsoft.scheduleweather.kakaomap.model.AddressItemDataSourceFactory;
import com.zerodsoft.scheduleweather.kakaomap.model.PlaceItemDataSource;
import com.zerodsoft.scheduleweather.kakaomap.model.PlaceItemDataSourceFactory;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddressViewModel extends ViewModel
{
    private LiveData<PagedList<AddressResponseDocuments>> pagedListLiveData;
    private AddressItemDataSourceFactory dataSourceFactory;
    private MutableLiveData<AddressItemDataSource> dataSourceMutableLiveData;
    private Executor executor;

    public AddressViewModel()
    {
    }

    public void init(LocalApiPlaceParameter addressParameter)
    {
        dataSourceFactory = new AddressItemDataSourceFactory(addressParameter);
        dataSourceMutableLiveData = dataSourceFactory.getLiveData();

        PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Integer.parseInt(LocalApiPlaceParameter.DEFAULT_SIZE))
                .setPageSize(1)
                .setPrefetchDistance(4)
                .build();
        executor = Executors.newFixedThreadPool(5);
        pagedListLiveData = new LivePagedListBuilder<Integer, AddressResponseDocuments>(dataSourceFactory, config)
                .setFetchExecutor(executor)
                .build();
    }


    public LiveData<PagedList<AddressResponseDocuments>> getPagedListMutableLiveData()
    {
        return pagedListLiveData;
    }
}
