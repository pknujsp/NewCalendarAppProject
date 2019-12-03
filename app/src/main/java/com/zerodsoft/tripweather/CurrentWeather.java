package com.zerodsoft.tripweather;

public class CurrentWeather {
    private String coord_longitude;
    private String coord_latitude;
    private String dt;
    private String sys_sunrise;
    private String sys_sunset;
    private String weather_id;
    private String weather_icon;
    private String main_temp;
    private String main_pressure;
    private String main_humidity;
    private String wind_speed;
    private String wind_degree;
    private String clouds_all;
    private String id;
    private String sys_country;
    private String name;
    private String weather_description;
    private String weather_main;
    private String visibility;

    public String getCoord_longitude() {
        return coord_longitude;
    }

    public CurrentWeather setCoord_longitude(String coord_longitude) {
        this.coord_longitude = coord_longitude;
        return this;
    }

    public String getCoord_latitude() {
        return coord_latitude;
    }

    public CurrentWeather setCoord_latitude(String coord_latitude) {
        this.coord_latitude = coord_latitude;
        return this;
    }

    public String getDt() {
        return dt;
    }

    public CurrentWeather setDt(String dt) {
        this.dt = dt;
        return this;
    }

    public String getSys_sunrise() {
        return sys_sunrise;
    }

    public CurrentWeather setSys_sunrise(String sys_sunrise) {
        this.sys_sunrise = sys_sunrise;
        return this;
    }

    public String getSys_sunset() {
        return sys_sunset;
    }

    public CurrentWeather setSys_sunset(String sys_sunset) {
        this.sys_sunset = sys_sunset;
        return this;
    }

    public String getWeather_id() {
        return weather_id;
    }

    public CurrentWeather setWeather_id(String weather_id) {
        this.weather_id = weather_id;
        return this;
    }

    public String getWeather_icon() {
        return weather_icon;
    }

    public CurrentWeather setWeather_icon(String weather_icon) {
        this.weather_icon = weather_icon;
        return this;
    }

    public String getMain_temp() {
        return main_temp;
    }

    public CurrentWeather setMain_temp(String main_temp) {
        this.main_temp = main_temp;
        return this;
    }

    public String getMain_pressure() {
        return main_pressure;
    }

    public CurrentWeather setMain_pressure(String main_pressure) {
        this.main_pressure = main_pressure;
        return this;
    }

    public String getMain_humidity() {
        return main_humidity;
    }

    public CurrentWeather setMain_humidity(String main_humidity) {
        this.main_humidity = main_humidity;
        return this;
    }

    public String getWind_speed() {
        return wind_speed;
    }

    public CurrentWeather setWind_speed(String wind_speed) {
        this.wind_speed = wind_speed;
        return this;
    }

    public String getWind_degree() {
        return wind_degree;
    }

    public CurrentWeather setWind_degree(String wind_degree) {
        this.wind_degree = wind_degree;
        return this;
    }

    public String getClouds_all() {
        return clouds_all;
    }

    public CurrentWeather setClouds_all(String clouds_all) {
        this.clouds_all = clouds_all;
        return this;
    }

    public String getId() {
        return id;
    }

    public CurrentWeather setId(String id) {
        this.id = id;
        return this;
    }

    public String getSys_country() {
        return sys_country;
    }

    public CurrentWeather setSys_country(String sys_country) {
        this.sys_country = sys_country;
        return this;
    }

    public String getName() {
        return name;
    }

    public CurrentWeather setName(String name) {
        this.name = name;
        return this;
    }

    public String getWeather_description() {
        return weather_description;
    }

    public CurrentWeather setWeather_description(String weather_description) {
        this.weather_description = weather_description;
        return this;
    }

    public String getWeather_main() {
        return weather_main;
    }

    public CurrentWeather setWeather_main(String weather_main) {
        this.weather_main = weather_main;
        return this;
    }

    public String getVisibility() {
        return visibility;
    }

    public CurrentWeather setVisibility(String visibility) {
        this.visibility = visibility;
        return this;
    }
}