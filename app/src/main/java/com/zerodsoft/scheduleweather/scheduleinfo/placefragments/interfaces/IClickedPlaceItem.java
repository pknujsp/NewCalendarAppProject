package com.zerodsoft.scheduleweather.scheduleinfo.placefragments.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

public interface IClickedPlaceItem
{
    void onClickedItem(PlaceDocuments document);
    void onClickedMore(String categoryDescription);
}
