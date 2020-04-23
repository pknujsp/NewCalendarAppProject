package com.zerodsoft.tripweather.DataCommunication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataCommunicationClient {
    private static final String CURRENT_WEATHER_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService/";
    private static Retrofit retrofit = null;

    public static DataDownloadService getApiService() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor.httpLoggingInterceptor()).build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(CURRENT_WEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(DataDownloadService.class);
    }
}
