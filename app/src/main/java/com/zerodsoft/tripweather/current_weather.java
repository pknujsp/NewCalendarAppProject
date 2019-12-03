package com.zerodsoft.tripweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

public class current_weather extends AppCompatActivity {
    TextView weatherDescription, currentTemp, humidity, speed, degree, cloudsall, dt, country, areaName, sunrise, sunset, getData;
    CurrentWeatherTextViewVO currentWeatherTextViewVO = new CurrentWeatherTextViewVO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_weather);

        getData = findViewById(R.id.btn_getdata);
        weatherDescription = findViewById(R.id.txt_weatherDescription);
        currentTemp = findViewById(R.id.txt_temp);
        humidity = findViewById(R.id.txt_humidity);
        speed = findViewById(R.id.txt_speed);
        degree = findViewById(R.id.txt_degree);
        cloudsall = findViewById(R.id.txt_cloudsall);
        dt = findViewById(R.id.txt_dt);
        country = findViewById(R.id.txt_country);
        areaName = findViewById(R.id.txt_areaname);
        sunrise = findViewById(R.id.txt_sunrise);
        sunset = findViewById(R.id.txt_sunset);

        currentWeatherTextViewVO.setWeatherDescription(weatherDescription);
        currentWeatherTextViewVO.setCurrentTemp(currentTemp);
        currentWeatherTextViewVO.setHumidity(humidity);
        currentWeatherTextViewVO.setSpeed(speed);
        currentWeatherTextViewVO.setDegree(degree);
        currentWeatherTextViewVO.setCloudsall(cloudsall);
        currentWeatherTextViewVO.setDt(dt);
        currentWeatherTextViewVO.setCountry(country);
        currentWeatherTextViewVO.setAreaName(areaName);
        currentWeatherTextViewVO.setSunrise(sunrise);
        currentWeatherTextViewVO.setSunset(sunset);
    }


    public void getWeatherData(View v) throws ExecutionException, InterruptedException {
        ConditionCode conditionCode = new ConditionCode();
        DownloadTask downloadTask = new DownloadTask();

        downloadTask.setTextViewVO(currentWeatherTextViewVO);
        downloadTask.execute();
        //downloadTask.get();
    }
}
