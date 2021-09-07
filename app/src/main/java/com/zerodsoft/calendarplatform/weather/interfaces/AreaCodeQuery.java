package com.zerodsoft.calendarplatform.weather.interfaces;

import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.room.dto.WeatherAreaCodeDTO;

import java.util.List;

public interface AreaCodeQuery {
	void getAreaCodes(double latitude, double longitude, DbQueryCallback<List<WeatherAreaCodeDTO>> callback);

	void getCodeOfProximateArea(double latitude, double longitude, DbQueryCallback<WeatherAreaCodeDTO> callback);

}
