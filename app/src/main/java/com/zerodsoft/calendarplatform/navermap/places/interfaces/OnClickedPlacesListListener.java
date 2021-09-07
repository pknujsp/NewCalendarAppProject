package com.zerodsoft.calendarplatform.navermap.places.interfaces;

import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.calendarplatform.room.dto.PlaceCategoryDTO;

public interface OnClickedPlacesListListener
{
    void onClickedItemInList(PlaceCategoryDTO placeCategory, PlaceDocuments placeDocument, int index);

    void onClickedMoreInList(PlaceCategoryDTO placeCategory);
}