package com.zerodsoft.scheduleweather.event.places.interfaces;

import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

public interface OnClickedPlacesListListener
{
    void onClickedItemInList(PlaceCategoryDTO placeCategory, int index);

    void onClickedMoreInList(PlaceCategoryDTO placeCategory);
}