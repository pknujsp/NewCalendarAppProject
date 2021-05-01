package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.zerodsoft.scheduleweather.navermap.BottomSheetType;

public interface BottomSheetController
{
    void setStateOfBottomSheet(BottomSheetType bottomSheetType, int state);

    int getStateOfBottomSheet(BottomSheetType bottomSheetType);
}