package com.zerodsoft.scheduleweather.kakaomap.interfaces;

public interface IMapToolbar
{
    public static final int MAP = 0;
    public static final int LIST = 1;
    public static final int ALL = 2;

    void changeOpenCloseMenuVisibility(boolean isSearching);

    void setMenuVisibility(int type, boolean state);

    void setText(String text);
}
