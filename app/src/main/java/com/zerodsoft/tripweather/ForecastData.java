package com.zerodsoft.tripweather;

public class ForecastData {
    private String list_dt;
    private String list_main_temp;
    private String list_main_humidity;

    private String list_weather_id;
    private String list_weather_icon;

    private String list_clouds_all;

    private String list_wind_speed;
    private String list_wind_degree;

    private String list_rain;
    private String list_snow;

    public String getList_dt() {
        return list_dt;
    }

    public ForecastData setList_dt(String list_dt) {
        this.list_dt = list_dt;
        return this;
    }

    public String getList_main_temp() {
        return list_main_temp;
    }

    public ForecastData setList_main_temp(String list_main_temp) {
        this.list_main_temp = list_main_temp;
        return this;
    }

    public String getList_main_humidity() {
        return list_main_humidity;
    }

    public ForecastData setList_main_humidity(String list_main_humidity) {
        this.list_main_humidity = list_main_humidity;
        return this;
    }

    public String getList_weather_id() {
        return list_weather_id;
    }

    public ForecastData setList_weather_id(String list_weather_id) {
        this.list_weather_id = list_weather_id;
        return this;
    }

    public String getList_weather_icon() {
        return list_weather_icon;
    }

    public ForecastData setList_weather_icon(String list_weather_icon) {
        this.list_weather_icon = list_weather_icon;
        return this;
    }

    public String getList_clouds_all() {
        return list_clouds_all;
    }

    public ForecastData setList_clouds_all(String list_clouds_all) {
        this.list_clouds_all = list_clouds_all;
        return this;
    }

    public String getList_wind_speed() {
        return list_wind_speed;
    }

    public ForecastData setList_wind_speed(String list_wind_speed) {
        this.list_wind_speed = list_wind_speed;
        return this;
    }

    public String getList_wind_degree() {
        return list_wind_degree;
    }

    public ForecastData setList_wind_degree(String list_wind_degree) {
        this.list_wind_degree = list_wind_degree;
        return this;
    }

    public String getList_rain() {
        return list_rain;
    }

    public ForecastData setList_rain(String list_rain) {
        this.list_rain = list_rain;
        return this;
    }

    public String getList_snow() {
        return list_snow;
    }

    public ForecastData setList_snow(String list_snow) {
        this.list_snow = list_snow;
        return this;
    }
}
