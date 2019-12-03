package com.zerodsoft.tripweather;

import android.os.AsyncTask;
import android.widget.TextView;

import java.util.Date;
import java.util.HashSet;

public class DownloadTask extends AsyncTask<Void, Void, Object[]> {
    private CurrentWeatherTextViewVO vo = null;

    protected void setTextViewVO(CurrentWeatherTextViewVO vo) {
        this.vo = vo;
    }

    @Override
    protected Object[] doInBackground(Void... voids) {

        OpenWeatherMapDownloader openWeatherMap = new OpenWeatherMapDownloader();
        Object[] objs = new Object[2];

        try {
            openWeatherMap.downloadData();

            objs[0] = openWeatherMap.getCurrentWeather();
            objs[1] = openWeatherMap.getForecastMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objs;
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        CurrentWeather currentWeather = (CurrentWeather) objects[0];
        ForecastWeather forecastWeather = (ForecastWeather) objects[1];

        vo.getSunrise().setText(convertUnixTime(currentWeather.getSys_sunrise()));
        vo.getSunset().setText(convertUnixTime(currentWeather.getSys_sunset()));
        vo.getCountry().setText(currentWeather.getSys_country());
        vo.getAreaName().setText(currentWeather.getName());
        vo.getCloudsall().setText(currentWeather.getClouds_all());
        vo.getWeatherDescription().setText(currentWeather.getWeather_description());
        vo.getCurrentTemp().setText(currentWeather.getMain_temp());
        vo.getHumidity().setText(currentWeather.getMain_humidity());
        vo.getDt().setText(convertUnixTime(currentWeather.getDt()));
        vo.getSpeed().setText(currentWeather.getWind_speed());
        vo.getDegree().setText(currentWeather.getWind_degree());
    }

    private String convertUnixTime(String unixTime) {
        return new Date(Long.valueOf(unixTime) * 1000).toString();
    }
}
