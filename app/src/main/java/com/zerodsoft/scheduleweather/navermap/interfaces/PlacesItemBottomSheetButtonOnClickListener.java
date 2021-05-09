package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;

public interface PlacesItemBottomSheetButtonOnClickListener
{
    void onSelectedLocation(KakaoLocalDocument kakaoLocalDocument);

    void onRemovedLocation();
}
