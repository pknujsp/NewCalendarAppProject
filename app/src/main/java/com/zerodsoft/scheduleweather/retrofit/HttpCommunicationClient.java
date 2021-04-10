package com.zerodsoft.scheduleweather.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
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

    private static final String KAKAO_PLACE_JSON_URL = "https://place.map.kakao.com/main/";

    private static final String SGIS_AUTH_SERVICE_URL = "https://sgisapi.kostat.go.kr/OpenAPI3/auth/";
    private static final String SGIS_TRANSFORMATION_SERVICE_URL = "https://sgisapi.kostat.go.kr/OpenAPI3/transformation/";
    private static final String SGIS_FIGURE_SERVICE_URL = "https://sgisapi.kostat.go.kr/OpenAPI3/figure/";

    //날씨
    public static final String VILAGE_FCST_INFO_SERVICE_SERVICE_KEY = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";
    public static final String MID_FCST_INFO_SERVICE_SERVICE_KEY = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";
    public static final String AIR_CONDITION_SERVICE_SERVICE_KEY = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";
    public static final String FIND_STATION_FOR_AIR_CONDITION_SERVICE_SERVICE_KEY = "T2nJm9zlOA0Z7Dut%2BThT6Jp0Itn0zZw80AUP3uMdOWlZJR1gVPkx9p1t8etuSW1kWsSNrGGHKdxbwr1IUlt%2Baw%3D%3D";

    //SGIS
    public static final String SGIS_CONSUMER_KEY = "57e8009093ec4be5af56";
    public static final String SGIS_CONSUMER_SECRET = "ccc314dc112d4283a984";
    public static String SGIS_ACCESS_TOKEN = null;

    public static final String DATATYPE = "JSON";

    private static volatile Retrofit kakaoInstance = null;
    private static volatile Retrofit vilageFcstInstance = null;
    private static volatile Retrofit midFcstInstance = null;
    private static volatile Retrofit airConditionInstance = null;
    private static volatile Retrofit findStationInstance = null;
    private static volatile Retrofit sgisAuthInstance = null;
    private static volatile Retrofit sgisTransformationInstance = null;
    private static volatile Retrofit sgisFigureInstance = null;
    private static volatile Retrofit kakaoPlaceInstance = null;

    public static final int KAKAO = 0;
    public static final int MID_FCST = 1;
    public static final int VILAGE_FCST = 2;
    public static final int AIR_CONDITION = 3;
    public static final int FIND_STATION_FOR_AIR_CONDITION = 4;
    public static final int SGIS_AUTH = 5;
    public static final int KAKAO_PLACE = 6;
    public static final int SGIS_TRANSFORMATION = 7;
    public static final int SGIS_FIGURE = 8;

    public static int lastService = -1;

    public static synchronized Querys getApiService(int serviceType)
    {
        switch (serviceType)
        {
            case KAKAO:
            {
                if (kakaoInstance == null)
                {
                    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                    Gson gson = new GsonBuilder().setLenient().create();

                    kakaoInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                            .baseUrl(KAKAO_LOCAL_API_URL).build();
                }
                return kakaoInstance.create(Querys.class);
            }

            case MID_FCST:
            {
                if (midFcstInstance == null)
                {
                    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                    Gson gson = new GsonBuilder().setLenient().create();

                    midFcstInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                            .baseUrl(MID_FCST_INFO_SERVICE_URL).build();
                }
                return midFcstInstance.create(Querys.class);
            }

            case VILAGE_FCST:
            {
                if (vilageFcstInstance == null)
                {
                    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                    Gson gson = new GsonBuilder().setLenient().create();

                    vilageFcstInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                            .baseUrl(VILAGE_FCST_INFO_SERVICE_URL).build();
                }
                return vilageFcstInstance.create(Querys.class);
            }

            case AIR_CONDITION:
            {
                if (airConditionInstance == null)
                {
                    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                    Gson gson = new GsonBuilder().setLenient().create();

                    airConditionInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                            .baseUrl(AIR_CONDITION_SERVICE_URL).build();
                }
                return airConditionInstance.create(Querys.class);
            }

            case FIND_STATION_FOR_AIR_CONDITION:
            {
                if (findStationInstance == null)
                {
                    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                    Gson gson = new GsonBuilder().setLenient().create();

                    findStationInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                            .baseUrl(FIND_STATION_FOR_AIR_CONDITION_SERVICE_URL).build();
                }
                return findStationInstance.create(Querys.class);
            }

            case SGIS_AUTH:
            {
                if (sgisAuthInstance == null)
                {
                    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build();
                    Gson gson = new GsonBuilder().setLenient().create();

                    sgisAuthInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                            .baseUrl(SGIS_AUTH_SERVICE_URL).build();
                }
                return sgisAuthInstance.create(Querys.class);
            }

            case KAKAO_PLACE:
            {
                if (kakaoPlaceInstance == null)
                {
                    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build();
                    Gson gson = new GsonBuilder().setLenient().create();

                    kakaoPlaceInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                            .baseUrl(KAKAO_PLACE_JSON_URL).build();
                }
                return kakaoPlaceInstance.create(Querys.class);
            }

            case SGIS_TRANSFORMATION:
            {
                if (sgisTransformationInstance == null)
                {
                    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build();
                    Gson gson = new GsonBuilder().setLenient().create();

                    sgisTransformationInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                            .baseUrl(SGIS_TRANSFORMATION_SERVICE_URL).build();
                }
                return sgisTransformationInstance.create(Querys.class);
            }

            case SGIS_FIGURE:
            {
                if (sgisFigureInstance == null)
                {
                    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build();
                    Gson gson = new GsonBuilder().setLenient().create();

                    sgisFigureInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
                            .baseUrl(SGIS_FIGURE_SERVICE_URL).build();
                }
                return sgisFigureInstance.create(Querys.class);
            }

            default:
                return null;

        }
    }

}
