package com.zerodsoft.calendarplatform.navermap.interfaces;

import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.KakaoLocalDocument;

public interface PlacesItemBottomSheetButtonOnClickListener
{
    void onSelectedLocation(KakaoLocalDocument kakaoLocalDocument);

    void onRemovedLocation();
}
