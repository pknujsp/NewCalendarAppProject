package com.zerodsoft.scheduleweather.navermap.interfaces;

public interface SearchBottomSheetController
{
    public static final int SEARCH_VIEW = 3;
    public static final int SEARCH_RESULT_VIEW = 4;

    void setSearchBottomSheetState(int state);

    int getSearchBottomSheetState();
}
