package com.zerodsoft.scheduleweather.kakaomap.interfaces;

public interface SearchFragmentController
{
    void closeSearchFragments(String currentFragmentTag);

    void setStateOfSearchBottomSheet(int state);

    int getStateOfSearchBottomSheet();
}
