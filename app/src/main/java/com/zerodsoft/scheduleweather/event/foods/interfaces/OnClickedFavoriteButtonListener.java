package com.zerodsoft.scheduleweather.event.foods.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

public interface OnClickedFavoriteButtonListener
{
    void onClickedFavoriteButton(String restaurantId, int groupPosition, int childPosition);

    void onClickedFavoriteButton(PlaceDocuments placeDocuments, int position);
}
