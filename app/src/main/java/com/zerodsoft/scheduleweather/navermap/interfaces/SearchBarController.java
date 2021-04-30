package com.zerodsoft.scheduleweather.navermap.interfaces;

public interface SearchBarController
{
    public static final int MAP = 0;
    public static final int LIST = 1;

    void setQuery(String query, boolean submit);

    void changeViewTypeImg(int type);

    void setViewTypeVisibility(int visibility);
}

