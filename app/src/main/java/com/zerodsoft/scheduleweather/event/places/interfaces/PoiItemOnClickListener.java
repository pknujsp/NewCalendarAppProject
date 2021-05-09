package com.zerodsoft.scheduleweather.event.places.interfaces;

import com.zerodsoft.scheduleweather.navermap.MarkerType;

public interface PoiItemOnClickListener<T>
{
    void onPOIItemSelectedByTouch(T e);

    void onPOIItemSelectedByList(int index, MarkerType markerType);

    void onPOIItemSelectedByBottomSheet(int index, MarkerType markerType);
}
