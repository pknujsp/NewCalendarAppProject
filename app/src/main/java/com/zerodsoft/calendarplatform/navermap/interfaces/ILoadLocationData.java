package com.zerodsoft.calendarplatform.navermap.interfaces;

import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;

public interface ILoadLocationData {
	void loadLocationData(int requestType, LocalApiPlaceParameter parameter, OnKakaoLocalApiCallback onKakaoLocalApiCallback);
}
