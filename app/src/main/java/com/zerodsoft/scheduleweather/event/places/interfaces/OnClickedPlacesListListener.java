package com.zerodsoft.scheduleweather.event.places.interfaces;

import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

public interface OnClickedPlacesListListener
{
    void onClickedItem(PlaceCategoryDTO placeCategory, int index);

    void onClickedMore(PlaceCategoryDTO placeCategory);
}