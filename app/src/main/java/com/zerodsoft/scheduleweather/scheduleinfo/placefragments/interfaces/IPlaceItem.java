package com.zerodsoft.scheduleweather.scheduleinfo.placefragments.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import java.util.List;

public interface IPlaceItem
{
    void onClickedItem(PlaceDocuments document);
    void onClickedMore(List<PlaceDocuments> placeDocuments, String categoryDescription);
}
