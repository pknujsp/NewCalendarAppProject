package com.zerodsoft.scheduleweather.kakaomap.viewmodel;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.kakaomap.interfaces.SearchHistoryQuery;
import com.zerodsoft.scheduleweather.kakaomap.model.SearchLocationHistoryRepository;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import java.util.List;

public class SearchHistoryViewModel extends AndroidViewModel implements SearchHistoryQuery
{
    private SearchLocationHistoryRepository repository;

    public SearchHistoryViewModel(@NonNull Application application)
    {
        super(application);
        this.repository = new SearchLocationHistoryRepository(application);
    }


    @Override
    public void insert(Integer type, String value, CarrierMessagingService.ResultCallback<SearchHistoryDTO> callback)
    {
        repository.insert(type, value, callback);
    }

    @Override
    public void select(Integer type, CarrierMessagingService.ResultCallback<List<SearchHistoryDTO>> callback)
    {
        repository.select(type, callback);
    }

    @Override
    public void select(Integer type, String value, CarrierMessagingService.ResultCallback<SearchHistoryDTO> callback)
    {
        repository.select(type, value, callback);
    }

    @Override
    public void delete(int id, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.delete(id, callback);
    }

    @Override
    public void delete(Integer type, String value, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.delete(type, value, callback);
    }

    @Override
    public void deleteAll(Integer type, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.deleteAll(type, callback);
    }

    @Override
    public void contains(Integer type, String value, CarrierMessagingService.ResultCallback<Boolean> callback)
    {
        repository.contains(type, value, callback);
    }
}
