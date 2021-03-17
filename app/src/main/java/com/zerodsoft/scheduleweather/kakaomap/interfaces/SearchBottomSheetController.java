package com.zerodsoft.scheduleweather.kakaomap.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

public interface SearchBottomSheetController
{
    public static final int SEARCH_VIEW = 3;
    public static final int SEARCH_RESULT_VIEW = 4;

    void setSearchBottomSheetState(int state);

    int getSearchBottomSheetState();
}
