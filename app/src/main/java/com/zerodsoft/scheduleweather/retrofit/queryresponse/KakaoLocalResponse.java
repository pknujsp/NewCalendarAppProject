package com.zerodsoft.scheduleweather.retrofit.queryresponse;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceKakaoLocalResponse;

public class KakaoLocalResponse
{
    public boolean isEmpty()
    {
        boolean isEmpty = false;

        if (this instanceof PlaceKakaoLocalResponse)
        {
            isEmpty = ((PlaceKakaoLocalResponse) this).getPlaceDocuments().isEmpty();
        } else if (this instanceof AddressKakaoLocalResponse)
        {
            isEmpty = ((AddressKakaoLocalResponse) this).getAddressResponseDocumentsList().isEmpty();
        }

        return isEmpty;
    }

    public int size()
    {
        int size = 0;

        if (this instanceof PlaceKakaoLocalResponse)
        {
            size = ((PlaceKakaoLocalResponse) this).getPlaceDocuments().size();
        } else if (this instanceof AddressKakaoLocalResponse)
        {
            size = ((AddressKakaoLocalResponse) this).getAddressResponseDocumentsList().size();
        }

        return size;
    }
}
