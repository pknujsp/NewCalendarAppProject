package com.zerodsoft.scheduleweather.event.places.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.util.List;

public interface PlaceItemsGetter
{
    List<PlaceDocuments> getPlaceItems(PlaceCategoryDTO placeCategoryDTO);
}
