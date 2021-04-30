package com.zerodsoft.scheduleweather.event.places.interfaces;

import com.zerodsoft.scheduleweather.navermap.PoiItemType;

public interface PoiItemOnClickListener<T>
{
    void onPOIItemSelectedByTouch(T e);

    void onPOIItemSelectedByList(int index, PoiItemType poiItemType);

    void onPOIItemSelectedByBottomSheet(int index, PoiItemType poiItemType);
}
