package com.zerodsoft.scheduleweather.weather.interfaces;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.utility.LonLat;

import java.util.List;

public interface AreaCodeQuery {
	void getAreaCodes(LonLat lonLat, CarrierMessagingService.ResultCallback<List<WeatherAreaCodeDTO>> callback);
}
