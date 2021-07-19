package com.zerodsoft.scheduleweather.navermap.interfaces;

import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;

public interface ILoadLocationData {
	void loadLocationData(int requestType, LocalApiPlaceParameter parameter, OnKakaoLocalApiCallback onKakaoLocalApiCallback);
}
