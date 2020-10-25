package com.zerodsoft.scheduleweather.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpCommunicationClient
{
    private static final String KAKAO_APP_KEY = "KakaoAK 7c9ce45e6c29183f85f43ad31833c902";

    private static final String KAKAO_LOCAL_API_URL = "https://dapi.kakao.com/v2/local/";
    private static final String MID_FCST_INFO_SERVICE_URL = "http://apis.data.go.kr/1360000/MidFcstInfoService/";
    private static final String VILAGE_FCST_INFO_SERVICE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService/";

    public static final String VILAGE_FCST_INFO_SERVICE_SERVICE_KEY = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";
    public static final String MID_FCST_INFO_SERVICE_SERVICE_KEY = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";
    public static final String DATATYPE = "JSON";

    private static Retrofit retrofit = null;
    private static OkHttpClient client = null;
    private static Gson gson = null;
    private static Retrofit.Builder builder = null;

    public static final int KAKAO = 0;
    public static final int MID_FCST = 1;
    public static final int VILAGE_FCST = 2;

    public static int lastService = -1;

    public static Querys getApiService(int serviceType)
    {
        if (client == null)
        {
            client = new OkHttpClient.Builder().build();
        }
/*
.addInterceptor(new Interceptor()
                    {
                        @Override
                        public Response intercept(Chain chain) throws IOException
                        {
                            Request newRequest = chain.request().newBuilder()
                                    .addHeader("Authorization", APP_KEY)
                                    .build();
                            return chain.proceed(newRequest);
                        }
 */
        if (retrofit == null || lastService != serviceType)
        {
            lastService = serviceType;

            gson = new GsonBuilder().setLenient().create();

            builder = new Retrofit.Builder()
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson));

            switch (serviceType)
            {
                case KAKAO:
                    builder.baseUrl(KAKAO_LOCAL_API_URL);
                    break;
                case MID_FCST:
                    builder.baseUrl(MID_FCST_INFO_SERVICE_URL);
                    break;
                case VILAGE_FCST:
                    builder.baseUrl(VILAGE_FCST_INFO_SERVICE_URL);
                    break;
            }
            retrofit = builder.build();
        }
        return retrofit.create(Querys.class);
    }
}
