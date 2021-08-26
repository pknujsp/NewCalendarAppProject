package com.zerodsoft.scheduleweather.navermap.places.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import java.util.List;

public interface BottomSheet
{
    void setBottomSheetState(int state);

    int getBottomSheetState();

    void setPlacesItems(List<PlaceDocuments> placeDocumentsList);

    void onClickedItem(int index);
}
