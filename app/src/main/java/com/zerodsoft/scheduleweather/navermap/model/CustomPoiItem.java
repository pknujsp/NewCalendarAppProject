package com.zerodsoft.scheduleweather.navermap.model;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;

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
