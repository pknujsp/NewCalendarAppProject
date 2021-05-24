package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.naver.maps.geometry.LatLng;

public interface IMapPoint
{
    double getLatitude();

    double getLongitude();

    LatLng getMapCenterPoint();

    void setMapVisibility(int visibility);
}
