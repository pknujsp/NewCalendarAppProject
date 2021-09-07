package com.zerodsoft.calendarplatform.navermap.places.interfaces;

import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import java.util.List;

public interface PlaceItemsGetter
{
    public void getPlaces(DbQueryCallback<List<PlaceDocuments>> callback, String categoryCode);
}
