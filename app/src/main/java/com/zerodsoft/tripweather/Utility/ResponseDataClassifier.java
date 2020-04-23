package com.zerodsoft.tripweather.Utility;

import android.content.Context;

import com.zerodsoft.tripweather.RequestResponse.CurrentWeatherResponseItem;
import com.zerodsoft.tripweather.WeatherData.CurrentWeather;

import java.util.List;


public class ResponseDataClassifier {
    public static CurrentWeather classifyCurrentWeatherResponseItem(List<CurrentWeatherResponseItem> items, Context context) {
        CurrentWeather currentWeather = new CurrentWeather(context);

        for (CurrentWeatherResponseItem item :
                items) {
            if (item.getCategory().equals("T1H")) {
                //기온
                currentWeather.setTemperature(item.getObsrValue());
            } else if (item.getCategory().equals("RN1")) {
                // 1시간 강수량
                currentWeather.setRainfall(item.getObsrValue());
            } else if (item.getCategory().equals("UUU")) {
                // 동서바람성분
                currentWeather.setEastWestWind(item.getObsrValue());
            } else if (item.getCategory().equals("VVV")) {
                //남북바람성분
                currentWeather.setSouthNorthWind(item.getObsrValue());
            } else if (item.getCategory().equals("REH")) {
                //습도
                currentWeather.setHumidity(item.getObsrValue());
            } else if (item.getCategory().equals("PTY")) {
                //강수형태
                currentWeather.setPrecipitationForm(item.getObsrValue());
            } else if (item.getCategory().equals("VEC")) {
                //풍향
                currentWeather.setWindDirection(item.getObsrValue());
            } else if (item.getCategory().equals("WSD")) {
                //풍속
                currentWeather.setWindSpeed(item.getObsrValue());
            }
        }
        return currentWeather;
    }
}
