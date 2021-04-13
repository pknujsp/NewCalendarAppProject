package com.zerodsoft.scheduleweather.common.interfaces;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;

public interface OnClickedBottomSheetListener<T>
{
    void onClickedBottomSheet(T e);
}
