package com.zerodsoft.tripweather;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONConversion {

    public HashMap<String, Object> convertForecastJson(String json) {
        HashMap<String, Object> forecastMap = new HashMap<>();

        try {
            JSONObject object_json = new JSONObject(json);

            JSONObject city_json = object_json.getJSONObject("city");
            JSONObject coord_json = city_json.getJSONObject("coord");
            JSONArray list_array = object_json.getJSONArray("list");

            JSONObject list_data = new JSONObject();
            JSONArray list_weather = new JSONArray();
            JSONObject list_weather2 = new JSONObject();
            JSONObject list_main = new JSONObject();
            JSONObject list_clouds = new JSONObject();
            JSONObject list_wind = new JSONObject();
            JSONObject list_rain = new JSONObject();
            JSONObject list_snow = new JSONObject();

            forecastMap.put("forecastData", new ArrayList<ForecastData>(list_array.length()));

            forecastMap.put("coord_lat", coord_json.getString("lat"));
            forecastMap.put("coord_lon", coord_json.getString("lon"));
            forecastMap.put("country", city_json.getString("country"));
            forecastMap.put("city_name", city_json.getString("name"));
            forecastMap.put("city_id", city_json.getString("id"));

            for (int index = 0; index < list_array.length(); index++) {
                list_data = (JSONObject) list_array.get(index);

                list_main = list_data.getJSONObject("main");

                list_weather = list_data.getJSONArray("weather");
                list_weather2 = (JSONObject) list_weather.get(0);
                list_clouds = list_data.getJSONObject("clouds");
                list_wind = list_data.getJSONObject("wind");

                ForecastData forecastData = new ForecastData();

                forecastData.setList_dt(list_data.getString("dt"));
                forecastData.setList_clouds_all(list_clouds.getString("all"));
                forecastData.setList_main_temp(list_main.getString("temp"));
                forecastData.setList_main_humidity(list_main.getString("humidity"));
                forecastData.setList_weather_id(list_weather2.getString("id"));
                forecastData.setList_weather_icon(list_weather2.getString("icon"));
                forecastData.setList_wind_speed(list_wind.getString("speed"));
                forecastData.setList_wind_degree(list_wind.getString("deg"));

                ((ArrayList<ForecastData>) forecastMap.get("forecastData")).add(forecastData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return forecastMap;
    }

    public CurrentWeather convertCurrentWeatherJson(String json) {
        CurrentWeather currentWeather = new CurrentWeather();

        try {
            JSONObject object_json = new JSONObject(json);

            JSONObject coord_json = object_json.getJSONObject("coord");
            JSONObject main_json = object_json.getJSONObject("main");
            JSONObject wind_json = object_json.getJSONObject("wind");
            JSONObject clouds_json = object_json.getJSONObject("clouds");
            JSONObject weather_json = object_json.getJSONArray("weather").getJSONObject(0);
            JSONObject sys_json = object_json.getJSONObject("sys");

            JSONArray weather_array = object_json.getJSONArray("weather");

            currentWeather.setCoord_longitude(coord_json.getString("lon"));
            currentWeather.setCoord_latitude(coord_json.getString("lat"));
            currentWeather.setMain_temp(main_json.getString("temp"));
            currentWeather.setMain_pressure(main_json.getString("pressure"));
            currentWeather.setMain_humidity(main_json.getString("humidity"));

            currentWeather.setWind_speed(wind_json.getString("speed"));
            currentWeather.setWind_degree(wind_json.getString("deg"));
            currentWeather.setClouds_all(clouds_json.getString("all"));

            currentWeather.setWeather_id(weather_json.getString("id"));
            currentWeather.setWeather_description(weather_json.getString("description"));
            currentWeather.setWeather_main(weather_json.getString("main"));

            currentWeather.setVisibility(object_json.getString("visibility"));

            currentWeather.setSys_country(sys_json.getString("country"));
            currentWeather.setSys_sunrise(sys_json.getString("sunrise"));
            currentWeather.setSys_sunset(sys_json.getString("sunset"));

            currentWeather.setDt(object_json.getString("dt"));
            currentWeather.setId(object_json.getString("id"));
            currentWeather.setName(object_json.getString("name"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentWeather;
    }
}
