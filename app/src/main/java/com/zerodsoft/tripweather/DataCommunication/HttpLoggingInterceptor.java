package com.zerodsoft.tripweather.DataCommunication;

public class HttpLoggingInterceptor {
    protected static okhttp3.logging.HttpLoggingInterceptor httpLoggingInterceptor() {

        okhttp3.logging.HttpLoggingInterceptor interceptor = new okhttp3.logging.HttpLoggingInterceptor(new okhttp3.logging.HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                android.util.Log.e("Data :", message + "");
            }
        });

        return interceptor.setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BODY);
    }
}
