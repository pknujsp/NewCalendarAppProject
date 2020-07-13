package com.zerodsoft.scheduleweather.Retrofit;

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
    private static final String KAKAO_LOCAL_API_URL = "https://dapi.kakao.com/v2/local/";
    private static final String APP_KEY = "KakaoAK 7c9ce45e6c29183f85f43ad31833c902";
    private static Retrofit retrofit = null;
    private static OkHttpClient client = null;
    private static Gson gson = null;

    public static Querys getApiService()
    {
        if (client == null)
        {
            client = new OkHttpClient.Builder()
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
                    }).build();
        }
        if (retrofit == null)
        {
            gson = new GsonBuilder().setLenient().create();

            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(KAKAO_LOCAL_API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(Querys.class);
    }
}
