package com.zerodsoft.scheduleweather.event.places.interfaces;

public interface SearchViewController
{
    void closeSearchView(int viewType);

    void setSearchViewQuery(String value, boolean submit);
}
