package com.zerodsoft.scheduleweather.event.places.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.util.List;

public interface IPlaceItem
{
    List<PlaceDocuments> getPlaceItems(PlaceCategoryDTO placeCategory);

    int getPlaceItemsSize(PlaceCategoryDTO placeCategory);
}
