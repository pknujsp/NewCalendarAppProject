package com.zerodsoft.scheduleweather.kakaomap.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

public interface IBottomSheet
{
    public static final int ADDRESS = 0;
    public static final int PLACE = 1;
    public static final int BOTTOM_SHEET = 2;

    void setState(int state);

    int getState();

    void setVisibility(int viewType, int state);

    void setAddress(AddressResponseDocuments documents);

    void setPlace(PlaceDocuments documents);

    void setItemVisibility(int state);

    void setFragmentVisibility(int state);
}
