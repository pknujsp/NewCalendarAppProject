package com.zerodsoft.scheduleweather.weather.interfaces;

import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;

import java.util.List;

public interface AreaCodeQuery {
	void getAreaCodes(double latitude, double longitude, DbQueryCallback<List<WeatherAreaCodeDTO>> callback);

	void getCodeOfProximateArea(double latitude, double longitude, DbQueryCallback<WeatherAreaCodeDTO> callback);

}
