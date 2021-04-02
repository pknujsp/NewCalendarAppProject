package com.zerodsoft.scheduleweather.kakaomap.place;

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

    public static void getPlaceData(String placeId, CarrierMessagingService.ResultCallback<DataWrapper<JsonObject>> resultCallback)
    {
        App.executorService.execute(new Runnable()
        {
            @SneakyThrows
            @Override
            public void run()
            {
                try
                {
                    String json = readJsonFromUrl(WEB_JSON_URL + placeId);

                    JsonObject jsonObject = (JsonObject) JsonParser.parseString(json);
                    DataWrapper<JsonObject> dataWrapper = new DataWrapper<>(jsonObject);
                    resultCallback.onReceiveResult(dataWrapper);
                } catch (Exception e)
                {
                    DataWrapper<JsonObject> dataWrapper = new DataWrapper<>(e);
                    resultCallback.onReceiveResult(dataWrapper);
                }
            }
        });

    }

    private static String readJsonFromUrl(String urlString) throws Exception
    {
        BufferedReader reader = null;
        try
        {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();

            int read;
            char[] chars = new char[1024];

            while ((read = reader.read(chars)) != -1)
            {
                buffer.append(chars, 0, read);
            }
            return buffer.toString();
        } finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }
    }
}
