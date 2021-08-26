package com.zerodsoft.scheduleweather.navermap.places;

import android.service.carrier.CarrierMessagingService;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import lombok.SneakyThrows;

public class KakaoPlace
{
    public static final String WEB_JSON_URL = "https://place.map.kakao.com/main/v/";
    public static final String WEB_URL = "https://place.map.kakao.com/m/";

    private KakaoPlace()
    {
    }

}
