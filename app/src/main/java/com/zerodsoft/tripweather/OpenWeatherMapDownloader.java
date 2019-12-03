package com.zerodsoft.tripweather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Base64.Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class OpenWeatherMapDownloader {
    private CurrentWeather currentWeather = null;
    private HashMap<String, Object> forecastMap = null;

    public void downloadData() throws Exception {
        final String appId = "b9b3f764f8e307e7bacd139fdd9e44db";
        final String currentUrl = "https://api.openweathermap.org/data/2.5/weather?";
        final String forecastUrl = "https://api.openweathermap.org/data/2.5/forecast?";
        final String areaName = "gimhae,kr";
        final String unitFormat = "metric";

        JSONConversion jsonConversion = new JSONConversion();

        try {

            // current
            URL currentObj = new URL(currentUrl + "q=" + areaName + "&units=" + unitFormat + "&appId=" + appId);
            HttpURLConnection currentConnection = (HttpURLConnection) currentObj.openConnection();
            currentConnection.setRequestMethod("POST");

            BufferedReader in = new BufferedReader(new InputStreamReader(currentConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // forecast
            URL forecastObj = new URL(forecastUrl + "q=" + areaName + "&units=" + unitFormat + "&appId=" + appId);
            HttpURLConnection forecastCon = (HttpURLConnection) forecastObj.openConnection();
            forecastCon.setRequestMethod("POST");

            BufferedReader in2 = new BufferedReader(new InputStreamReader(forecastCon.getInputStream()));
            String inputLine2;
            StringBuffer response2 = new StringBuffer();

            while ((inputLine2 = in2.readLine()) != null) {
                response2.append(inputLine2);
            }
            in2.close();

            currentWeather = jsonConversion.convertCurrentWeatherJson(response.toString());
            forecastMap = jsonConversion.convertForecastJson(response2.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public HashMap<String, Object> getForecastMap() {
        return forecastMap;
    }
}
