package com.zerodsoft.scheduleweather.event.places.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.util.List;

public interface IClickedPlaceItem
{
    void onClickedItem(int index, PlaceCategoryDTO placeCategory, List<PlaceDocuments> placeDocumentsList);

    void onClickedMore(PlaceCategoryDTO placeCategory, List<PlaceDocuments> placeDocumentsList);
}
