package com.zerodsoft.scheduleweather.event.places.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

public interface OnClickedPlacesListListener
{
    void onClickedItemInList(PlaceCategoryDTO placeCategory, PlaceDocuments placeDocument, int index);

    void onClickedMoreInList(PlaceCategoryDTO placeCategory);
}