package com.zerodsoft.scheduleweather.scheduleinfo.placefragments.categoryview.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.categoryview.model.PlaceItemDataSource;
import com.zerodsoft.scheduleweather.scheduleinfo.placefragments.categoryview.model.PlaceItemDataSourceFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlacesAroundLocationViewModel extends ViewModel
{
    private LiveData<PagedList<PlaceDocuments>> pagedListLiveData;
    private PlaceItemDataSourceFactory dataSourceFactory;
    private MutableLiveData<PlaceItemDataSource> dataSourceMutableLiveData;
    private Executor executor;

    public PlacesAroundLocationViewModel()
    {
    }

    public void init(LocalApiPlaceParameter placeParameter)
    {
        dataSourceFactory = new PlaceItemDataSourceFactory(placeParameter);
        dataSourceMutableLiveData = dataSourceFactory.getLiveData();

        PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(Integer.parseInt(LocalApiPlaceParameter.DEFAULT_SIZE))
                .setPageSize(1)
                .setPrefetchDistance(4)
                .build();
        executor = Executors.newFixedThreadPool(5);
        pagedListLiveData = new LivePagedListBuilder<Integer, PlaceDocuments>(dataSourceFactory, config)
                .setFetchExecutor(executor)
                .build();
    }

    public LiveData<PagedList<PlaceDocuments>> getPagedListMutableLiveData()
    {
        return pagedListLiveData;
    }
}
