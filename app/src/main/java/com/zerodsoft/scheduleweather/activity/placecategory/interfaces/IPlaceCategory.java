package com.zerodsoft.scheduleweather.activity.placecategory.interfaces;

import android.service.carrier.CarrierMessagingService;

public interface IPlaceCategory
{
    //custom
    void insertCustom(String code);

    void selectCustom();

    void updateCustom(String previousCode, String code);

    void deleteCustom(String code);

    void deleteAllCustom();

    void containsCode(String code, CarrierMessagingService.ResultCallback<Boolean> callback);

    //selected
    void insertSelected(String code);

    void deleteSelected(String code);

    void deleteAllSelected();

    void selectSelected();

    void getSettingsData();
}
