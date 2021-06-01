package com.zerodsoft.scheduleweather.navermap.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.SearchHistoryDTO;

import java.util.List;

public interface SearchHistoryQuery
{
    void insert(Integer type, String value, CarrierMessagingService.ResultCallback<SearchHistoryDTO> callback);

    void select(Integer type, CarrierMessagingService.ResultCallback<List<SearchHistoryDTO>> callback);

    void select(Integer type, String value, CarrierMessagingService.ResultCallback<SearchHistoryDTO> callback);

    void delete(int id, CarrierMessagingService.ResultCallback<Boolean> callback);

    void delete(Integer type, String value, CarrierMessagingService.ResultCallback<Boolean> callback);

    void deleteAll(Integer type, CarrierMessagingService.ResultCallback<Boolean> callback);

    void contains(Integer type, String value, DbQueryCallback<Boolean> callback);
}
