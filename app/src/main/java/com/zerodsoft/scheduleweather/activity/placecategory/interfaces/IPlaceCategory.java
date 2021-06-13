package com.zerodsoft.scheduleweather.activity.placecategory.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.activity.placecategory.model.PlaceCategoryData;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.CustomPlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SelectedPlaceCategoryDTO;

import java.util.List;

public interface IPlaceCategory
{
    //custom
    void insertCustom(String code, CarrierMessagingService.ResultCallback<CustomPlaceCategoryDTO> callback);

    void selectCustom(CarrierMessagingService.ResultCallback<List<CustomPlaceCategoryDTO>> callback);

    void updateCustom(String currentCode, String code, CarrierMessagingService.ResultCallback<CustomPlaceCategoryDTO> callback);

    void deleteCustom(String code, CarrierMessagingService.ResultCallback<Boolean> callback);

    void deleteAllCustom(CarrierMessagingService.ResultCallback<Boolean> callback);

    void containsCode(String code, CarrierMessagingService.ResultCallback<Boolean> callback);

    //selected
    void insertSelected(String code, CarrierMessagingService.ResultCallback<SelectedPlaceCategoryDTO> callback);

    void insertAllSelected(List<PlaceCategoryDTO> list, CarrierMessagingService.ResultCallback<List<SelectedPlaceCategoryDTO>> callback);

    void deleteSelected(String code, CarrierMessagingService.ResultCallback<Boolean> callback);

    void deleteAllSelected(CarrierMessagingService.ResultCallback<Boolean> callback);

    void selectSelected(CarrierMessagingService.ResultCallback<List<SelectedPlaceCategoryDTO>> callback);

    void getSettingsData(CarrierMessagingService.ResultCallback<PlaceCategoryData> callback);

    void updateSelected(String currentCode, String code, CarrierMessagingService.ResultCallback<SelectedPlaceCategoryDTO> callback);

    void selectConvertedSelected(DbQueryCallback<List<PlaceCategoryDTO>> callback);
}
