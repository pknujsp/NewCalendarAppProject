package com.zerodsoft.scheduleweather.event.places.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import java.util.List;

public interface BottomSheet
{
    void setBottomSheetState(int state);

    void setPlacesItems(List<PlaceDocuments> placeDocumentsList);

    void onClickedItem(int index);
}
