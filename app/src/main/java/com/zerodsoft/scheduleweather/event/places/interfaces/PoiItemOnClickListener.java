package com.zerodsoft.scheduleweather.event.places.interfaces;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapView;

public interface PoiItemOnClickListener
{
    void onPOIItemSelectedByTouch(MapView mapView, MapPOIItem mapPOIItem);

    void onPOIItemSelectedByList(int index);

    void onPOIItemSelectedByBottomSheet(int index);
}
