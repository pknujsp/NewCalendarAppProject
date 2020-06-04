package com.zerodsoft.scheduleweather.Room;

import com.zerodsoft.scheduleweather.WeatherData.ForecastAreaData;

import java.util.ArrayList;

public interface OnDataListener
{
    void insertNForecastData(ArrayList<ForecastAreaData> nForecastDataList);
}