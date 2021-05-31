package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.naver.maps.geometry.LatLng;

import java.io.Serializable;

public interface IMapPoint extends Serializable {
	double getLatitude();

	double getLongitude();

	LatLng getMapCenterPoint();

	void setMapVisibility(int visibility);
}
