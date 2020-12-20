package com.zerodsoft.scheduleweather.scheduleinfo.placefragments.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import java.util.List;

public interface IPlaceItem
{
    List<PlaceDocuments> getPlaceItems(String categoryName);

    List<String> getCategoryNames();

    int getPlaceItemsSize(String categoryName);
}
