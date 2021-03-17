package com.zerodsoft.scheduleweather.kakaomap.model;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import net.daum.mf.map.api.MapPOIItem;

public class CustomPoiItem extends MapPOIItem
{
    private KakaoLocalDocument kakaoLocalDocument;

    public CustomPoiItem()
    {
    }

    public void setKakaoLocalDocument(KakaoLocalDocument kakaoLocalDocument)
    {
        this.kakaoLocalDocument = kakaoLocalDocument;
    }

    public KakaoLocalDocument getKakaoLocalDocument()
    {
        return kakaoLocalDocument;
    }
}
