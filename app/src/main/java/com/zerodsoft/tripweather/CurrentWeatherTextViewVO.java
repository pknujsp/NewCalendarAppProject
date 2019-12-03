package com.zerodsoft.tripweather;

import android.widget.TextView;

public class CurrentWeatherTextViewVO {
    private TextView weatherId;
    private TextView weatherDescription;
    private TextView visibility;
    private TextView currentTemp;
    private TextView humidity;
    private TextView speed;
    private TextView degree;
    private TextView cloudsall;
    private TextView dt;
    private TextView country;
    private TextView areaName;
    private TextView sunrise;
    private TextView sunset;

    public TextView getWeatherDescription() {
        return weatherDescription;
    }

    public CurrentWeatherTextViewVO setWeatherDescription(TextView weatherDescription) {
        this.weatherDescription = weatherDescription;
        return this;
    }

    public TextView getVisibility() {
        return visibility;
    }

    public CurrentWeatherTextViewVO setVisibility(TextView visibility) {
        this.visibility = visibility;
        return this;
    }

    public TextView getWeatherId() {
        return weatherId;
    }

    public CurrentWeatherTextViewVO setWeatherId(TextView weatherId) {
        this.weatherId = weatherId;
        return this;
    }

    public TextView getCurrentTemp() {
        return currentTemp;
    }

    public CurrentWeatherTextViewVO setCurrentTemp(TextView currentTemp) {
        this.currentTemp = currentTemp;
        return this;
    }

    public TextView getHumidity() {
        return humidity;
    }

    public CurrentWeatherTextViewVO setHumidity(TextView humidity) {
        this.humidity = humidity;
        return this;
    }

    public TextView getSpeed() {
        return speed;
    }

    public CurrentWeatherTextViewVO setSpeed(TextView speed) {
        this.speed = speed;
        return this;
    }

    public TextView getDegree() {
        return degree;
    }

    public CurrentWeatherTextViewVO setDegree(TextView degree) {
        this.degree = degree;
        return this;
    }

    public TextView getCloudsall() {
        return cloudsall;
    }

    public CurrentWeatherTextViewVO setCloudsall(TextView cloudsall) {
        this.cloudsall = cloudsall;
        return this;
    }

    public TextView getDt() {
        return dt;
    }

    public CurrentWeatherTextViewVO setDt(TextView dt) {
        this.dt = dt;
        return this;
    }

    public TextView getCountry() {
        return country;
    }

    public CurrentWeatherTextViewVO setCountry(TextView country) {
        this.country = country;
        return this;
    }

    public TextView getAreaName() {
        return areaName;
    }

    public CurrentWeatherTextViewVO setAreaName(TextView areaName) {
        this.areaName = areaName;
        return this;
    }

    public TextView getSunrise() {
        return sunrise;
    }

    public CurrentWeatherTextViewVO setSunrise(TextView sunrise) {
        this.sunrise = sunrise;
        return this;
    }

    public TextView getSunset() {
        return sunset;
    }

    public CurrentWeatherTextViewVO setSunset(TextView sunset) {
        this.sunset = sunset;
        return this;
    }


}