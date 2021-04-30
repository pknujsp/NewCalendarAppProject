package com.zerodsoft.scheduleweather.navermap.interfaces;

public interface BottomSheetController
{
    void setStateOfBottomSheet(String fragmentTag, int state);

    int getStateOfBottomSheet(String fragmentTag);
}
