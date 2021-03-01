package com.zerodsoft.scheduleweather.kakaomap.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.kakaomap.model.PlaceItemDataSource;
import com.zerodsoft.scheduleweather.kakaomap.model.PlaceItemDataSourceFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlacesViewModel extends ViewModel
{
    private LiveData<PagedList<PlaceDocuments>> pagedListLiveData;
    private PlaceItemDataSourceFactory dataSourceFactory;
    private MutableLiveData<PlaceItemDataSource> dataSourceMutableLiveData;
    private Executor executor;
    private PagedList.Config config;

    public PlacesViewModel()
    {
        executor = Executors.newFixedThreadPool(5);
        pagedListLiveData = new MutableLiveData<>();
    }

    public void init(LocalApiPlaceParameter placeParameter)
    {
        dataSourceFactory = new PlaceItemDataSourceFactory(placeParameter);
        dataSourceMutableLiveData = dataSourceFactory.getLiveData();

        config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Integer.parseInt(LocalApiPlaceParameter.DEFAULT_SIZE) * 2)
                .setPageSize(15)
                .setPrefetchDistance(4)
                .build();
        pagedListLiveData = new LivePagedListBuilder<Integer, PlaceDocuments>(dataSourceFactory, config)
                .setFetchExecutor(executor)
                .build();

    }

    public LiveData<PagedList<PlaceDocuments>> getPagedListMutableLiveData()
    {
        return pagedListLiveData;
    }

}
