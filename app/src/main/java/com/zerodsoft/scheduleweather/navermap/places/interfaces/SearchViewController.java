package com.zerodsoft.scheduleweather.navermap.places.interfaces;

public interface SearchViewController
{
    void closeSearchView(int viewType);

    void setSearchViewQuery(String value, boolean submit);
}
