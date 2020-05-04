package com.zerodsoft.tripweather.Room;

import com.zerodsoft.tripweather.WeatherData.ForecastAreaData;

import java.util.ArrayList;

public interface OnDataListener
{
    void insertNForecastData(ArrayList<ForecastAreaData> nForecastDataList);
}