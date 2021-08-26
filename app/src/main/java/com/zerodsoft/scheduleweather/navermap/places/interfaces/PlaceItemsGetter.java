package com.zerodsoft.scheduleweather.navermap.places.interfaces;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import java.util.List;

public interface PlaceItemsGetter
{
    public void getPlaces(DbQueryCallback<List<PlaceDocuments>> callback, String categoryCode);
}
