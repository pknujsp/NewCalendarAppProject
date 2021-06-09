package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.naver.maps.geometry.LatLng;

import java.io.Serializable;

public interface IMapPoint extends Serializable {
	LatLng getMapCenterPoint();
}
