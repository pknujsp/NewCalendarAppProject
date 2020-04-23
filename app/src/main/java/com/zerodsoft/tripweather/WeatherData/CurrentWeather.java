package com.zerodsoft.tripweather.WeatherData;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

public class CurrentWeather {
    private String areaName;
    private String areaCode;
    private String areaX;
    private String areaY;
    private String rainfall;
    private String eastWestWind;
    private String southNorthWind;
    private String humidity;
    private String precipitationForm;
    private String windDirection;
    private String windSpeed;
    private String temperature;
    private Context context;

    public CurrentWeather(Context context) {
        this.context = context;
    }

    public String getAreaName() {
        return areaName;
    }

    public CurrentWeather setAreaName(String areaName) {
        this.areaName = areaName;
        return this;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public CurrentWeather setAreaCode(String areaCode) {
        this.areaCode = areaCode;
        return this;
    }

    public String getAreaX() {
        return areaX;
    }

    public CurrentWeather setAreaX(String areaX) {
        this.areaX = areaX;
        return this;
    }

    public String getAreaY() {
        return areaY;
    }

    public CurrentWeather setAreaY(String areaY) {
        this.areaY = areaY;
        return this;
    }

    public String getRainfall() {
        return rainfall;
    }

    public CurrentWeather setRainfall(String rainfall) {
        this.rainfall = rainfall;
        return this;
    }

    public String getEastWestWind() {
        return eastWestWind;
    }

    public CurrentWeather setEastWestWind(String eastWestWind) {
        this.eastWestWind = eastWestWind;
        return this;
    }

    public String getSouthNorthWind() {
        return southNorthWind;
    }

    public CurrentWeather setSouthNorthWind(String southNorthWind) {
        this.southNorthWind = southNorthWind;
        return this;
    }

    public String getHumidity() {
        return humidity;
    }

    public CurrentWeather setHumidity(String humidity) {
        this.humidity = humidity;
        return this;
    }

    public String getPrecipitationForm() {
        return precipitationForm;
    }

    public CurrentWeather setPrecipitationForm(String precipitationForm) {
        this.precipitationForm = DataConverter.convertPrecipitationForm(precipitationForm, context);
        return this;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public CurrentWeather setWindDirection(String windDirection) {
        this.windDirection = DataConverter.convertWindDirection(windDirection, context);
        return this;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public CurrentWeather setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
        return this;
    }

    public String getTemperature() {
        return temperature;
    }

    public CurrentWeather setTemperature(String temperature) {
        this.temperature = temperature;
        return this;
    }

}
