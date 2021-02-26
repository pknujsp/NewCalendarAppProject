package com.zerodsoft.scheduleweather.activity.placecategory.interfaces;

public interface IPlaceCategory
{
    //custom
    void insertCustom(String code);

    void selectCustom();

    void updateCustom(int id, String code);

    void deleteCustom(int id);

    void deleteAllCustom();

    //selected
    void insertSelected(String code);

    void deleteSelected(String code);

    void deleteAllSelected();

    void selectSelected();

    void getSettingsData();
}
