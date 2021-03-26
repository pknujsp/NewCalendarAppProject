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

    private static final String AIR_CONDITION_SERVICE_URL = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/";
    private static final String FIND_STATION_FOR_AIR_CONDITION_SERVICE_URL = "http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/";

    private static final String SGIS_SERVICE_URL = "https://sgisapi.kostat.go.kr/OpenAPI3/";

    public static final String VILAGE_FCST_INFO_SERVICE_SERVICE_KEY = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";
    public static final String MID_FCST_INFO_SERVICE_SERVICE_KEY = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";
    public static final String AIR_CONDITION_SERVICE_SERVICE_KEY = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";
    public static final String FIND_STATION_FOR_AIR_CONDITION_SERVICE_SERVICE_KEY = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";
    public static final String SGIS_CONSUMER_KEY = "57e8009093ec4be5af56";
    public static final String SGIS_CONSUMER_SECRET = "ccc314dc112d4283a984";

    public static final String DATATYPE = "JSON";

    private static volatile Retrofit kakaoInstance = null;
    private static volatile Retrofit vilageFcstInstance = null;
    private static volatile Retrofit midFcstInstance = null;
    private static volatile Retrofit airConditionInstance = null;
    private static volatile Retrofit findStationInstance = null;
    private static volatile Retrofit sgisInstance = null;

    public static final int KAKAO = 0;
    public static final int MID_FCST = 1;
    public static final int VILAGE_FCST = 2;
    public static final int AIR_CONDITION = 3;
    public static final int FIND_STATION_FOR_AIR_CONDITION = 4;
    public static final int SGIS = 5;

    public static int lastService = -1;

    public static synchronized Querys getApiService(int serviceType)
    {
        if (serviceType == KAKAO)
        {
            if (kakaoInstance == null)
            {
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                Gson gson = new GsonBuilder().setLenient().create();

                kakaoInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                        .baseUrl(KAKAO_LOCAL_API_URL).build();
            }
            return kakaoInstance.create(Querys.class);
        } else if (serviceType == VILAGE_FCST)
        {
            if (vilageFcstInstance == null)
            {
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                Gson gson = new GsonBuilder().setLenient().create();

                vilageFcstInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                        .baseUrl(VILAGE_FCST_INFO_SERVICE_URL).build();
            }
            return vilageFcstInstance.create(Querys.class);
        } else if (serviceType == MID_FCST)
        {
            if (midFcstInstance == null)
            {
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                Gson gson = new GsonBuilder().setLenient().create();

                midFcstInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                        .baseUrl(MID_FCST_INFO_SERVICE_URL).build();
            }
            return midFcstInstance.create(Querys.class);
        } else if (serviceType == AIR_CONDITION)
        {
            if (airConditionInstance == null)
            {
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                Gson gson = new GsonBuilder().setLenient().create();

                airConditionInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                        .baseUrl(AIR_CONDITION_SERVICE_URL).build();
            }
            return airConditionInstance.create(Querys.class);
        } else if (serviceType == FIND_STATION_FOR_AIR_CONDITION)
        {
            if (findStationInstance == null)
            {
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                Gson gson = new GsonBuilder().setLenient().create();

                findStationInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                        .baseUrl(FIND_STATION_FOR_AIR_CONDITION_SERVICE_URL).build();
            }
            return findStationInstance.create(Querys.class);
        } else
        {
            if (sgisInstance == null)
            {
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                Gson gson = new GsonBuilder().setLenient().create();

                sgisInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                        .baseUrl(SGIS_SERVICE_URL).build();
            }
            return sgisInstance.create(Querys.class);
        }
    }
}
