package com.zerodsoft.calendarplatform.navermap.places.interfaces;

import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import java.util.List;

public interface BottomSheet
{
    void setBottomSheetState(int state);

    int getBottomSheetState();

    void setPlacesItems(List<PlaceDocuments> placeDocumentsList);

    void onClickedItem(int index);
}
