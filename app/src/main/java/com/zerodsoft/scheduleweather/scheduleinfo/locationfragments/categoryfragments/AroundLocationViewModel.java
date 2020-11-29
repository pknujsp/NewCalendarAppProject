package com.zerodsoft.scheduleweather.scheduleinfo.locationfragments.categoryfragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategory;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

public class AroundLocationViewModel extends ViewModel
{
    private LiveData<PagedList<PlaceDocuments>> pagedListLiveData;

    public AroundLocationViewModel()
    {
        AroundLocationRepository repository = new AroundLocationRepository();
        pagedListLiveData = repository.getPagedListLiveData();
    }

    public LiveData<PagedList<PlaceDocuments>> getPagedListMutableLiveData()
    {
        return pagedListLiveData;
    }
}
