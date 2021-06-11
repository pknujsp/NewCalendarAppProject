package com.zerodsoft.scheduleweather.event.places.interfaces;

import com.zerodsoft.scheduleweather.navermap.MarkerType;

public interface PoiItemOnClickListener {

	void onPOIItemSelectedByList(int index, MarkerType markerType);

	void onPOIItemSelectedByBottomSheet(int index, MarkerType markerType);
}
