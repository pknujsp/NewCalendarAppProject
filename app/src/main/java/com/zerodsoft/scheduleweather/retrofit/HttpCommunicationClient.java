package com.zerodsoft.scheduleweather.retrofit;

import android.app.DownloadManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpCommunicationClient
{
    public static final String KAKAO_APP_KEY = "KakaoAK 7c9ce45e6c29183f85f43ad31833c902";

    private static final String KAKAO_LOCAL_API_URL = "https://dapi.kakao.com/v2/local/";
    private static final String MID_FCST_INFO_SERVICE_URL = "http://apis.data.go.kr/1360000/MidFcstInfoService/";
    private static final String VILAGE_FCST_INFO_SERVICE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService/";

    public static final String VILAGE_FCST_INFO_SERVICE_SERVICE_KEY = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";
    public static final String MID_FCST_INFO_SERVICE_SERVICE_KEY = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";
    public static final String DATATYPE = "JSON";

    private static volatile Retrofit kakaoInstance = null;
    private static volatile Retrofit vilageFcstInstance = null;
    private static volatile Retrofit midFcstInstance = null;
    private static volatile OkHttpClient client = null;
    private static volatile Gson gson = null;

    public static final int KAKAO = 0;
    public static final int MID_FCST = 1;
    public static final int VILAGE_FCST = 2;

    public static int lastService = -1;

    static
    {
        client = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).build();
        gson = new GsonBuilder().setLenient().create();
    }

    public static synchronized Querys getApiService(int serviceType)
    {
        if (serviceType == KAKAO)
        {
            if (kakaoInstance == null)
            {
                kakaoInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                        .baseUrl(KAKAO_LOCAL_API_URL).build();
            }
            return kakaoInstance.create(Querys.class);
        } else if (serviceType == VILAGE_FCST)
        {
            if (vilageFcstInstance == null)
            {
                vilageFcstInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                        .baseUrl(VILAGE_FCST_INFO_SERVICE_URL).build();
            }
            return vilageFcstInstance.create(Querys.class);
        } else
        {
            if (midFcstInstance == null)
            {
                midFcstInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                        .baseUrl(MID_FCST_INFO_SERVICE_URL).build();
            }
            return midFcstInstance.create(Querys.class);
        }
    }
}
