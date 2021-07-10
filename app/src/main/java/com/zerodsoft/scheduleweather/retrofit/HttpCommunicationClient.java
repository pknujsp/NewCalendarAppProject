package com.zerodsoft.scheduleweather.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class HttpCommunicationClient {
	public static final String KAKAO_APP_KEY = "KakaoAK 7c9ce45e6c29183f85f43ad31833c902";
	private static final String KAKAO_LOCAL_API_URL = "https://dapi.kakao.com/v2/local/";

	private static final String MID_FCST_INFO_SERVICE_URL = "http://apis.data.go.kr/1360000/MidFcstInfoService/";
	private static final String VILAGE_FCST_INFO_SERVICE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/";

	private static final String AIR_CONDITION_SERVICE_URL = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/";
	private static final String FIND_STATION_FOR_AIR_CONDITION_SERVICE_URL = "http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/";

	private static final String KAKAO_PLACE_JSON_URL = "https://place.map.kakao.com/main/";

	private static final String SGIS_AUTH_SERVICE_URL = "https://sgisapi.kostat.go.kr/OpenAPI3/auth/";
	private static final String SGIS_TRANSFORMATION_SERVICE_URL = "https://sgisapi.kostat.go.kr/OpenAPI3/transformation/";
	private static final String SGIS_FIGURE_SERVICE_URL = "https://sgisapi.kostat.go.kr/OpenAPI3/figure/";
	private static final String SGIS_ADDRESS_SERVICE_URL = "https://sgisapi.kostat.go.kr/OpenAPI3/addr/";

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

	public static final int KAKAO = 0;
	public static final int MID_FCST = 1;
	public static final int VILAGE_FCST = 2;
	public static final int AIR_CONDITION = 3;
	public static final int FIND_STATION_FOR_AIR_CONDITION = 4;
	public static final int SGIS_AUTH = 5;
	public static final int KAKAO_PLACE = 6;
	public static final int SGIS_TRANSFORMATION = 7;
	public static final int SGIS_FIGURE = 8;
	public static final int SGIS_ADDRESS = 9;

	public static synchronized Querys getApiService(int serviceType) {
		switch (serviceType) {
			case KAKAO: {
				OkHttpClient client = new OkHttpClient.Builder().connectTimeout(8, TimeUnit.SECONDS).build();
				Gson gson = new GsonBuilder().setLenient().create();

				Retrofit kakaoInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
						.baseUrl(KAKAO_LOCAL_API_URL).build();

				return kakaoInstance.create(Querys.class);
			}

			case MID_FCST: {
				OkHttpClient client = new OkHttpClient.Builder().connectTimeout(8, TimeUnit.SECONDS).build();
				Gson gson = new GsonBuilder().setLenient().create();

				Retrofit midFcstInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create())
						.addConverterFactory(ScalarsConverterFactory.create())
						.baseUrl(MID_FCST_INFO_SERVICE_URL).build();
				return midFcstInstance.create(Querys.class);
			}

			case VILAGE_FCST: {
				OkHttpClient client = new OkHttpClient.Builder().connectTimeout(8, TimeUnit.SECONDS).build();
				Gson gson = new GsonBuilder().setLenient().create();

				Retrofit vilageFcstInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create())
						.addConverterFactory(ScalarsConverterFactory.create())
						.baseUrl(VILAGE_FCST_INFO_SERVICE_URL).build();
				return vilageFcstInstance.create(Querys.class);
			}

			case AIR_CONDITION: {

				OkHttpClient client = new OkHttpClient.Builder().connectTimeout(8, TimeUnit.SECONDS).build();
				Gson gson = new GsonBuilder().setLenient().create();

				Retrofit airConditionInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create())
						.addConverterFactory(ScalarsConverterFactory.create())
						.baseUrl(AIR_CONDITION_SERVICE_URL).build();

				return airConditionInstance.create(Querys.class);
			}

			case FIND_STATION_FOR_AIR_CONDITION: {

				OkHttpClient client = new OkHttpClient.Builder().connectTimeout(8, TimeUnit.SECONDS).build();
				Gson gson = new GsonBuilder().setLenient().create();

				Retrofit findStationInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create())
						.addConverterFactory(ScalarsConverterFactory.create())
						.baseUrl(FIND_STATION_FOR_AIR_CONDITION_SERVICE_URL).build();
				return findStationInstance.create(Querys.class);
			}

			case SGIS_AUTH: {
				OkHttpClient client = new OkHttpClient.Builder().connectTimeout(8, TimeUnit.SECONDS).build();
				Gson gson = new GsonBuilder().setLenient().create();

				Retrofit sgisAuthInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
						.baseUrl(SGIS_AUTH_SERVICE_URL).build();
				return sgisAuthInstance.create(Querys.class);
			}

			case KAKAO_PLACE: {
				OkHttpClient client = new OkHttpClient.Builder().connectTimeout(8, TimeUnit.SECONDS).build();
				Gson gson = new GsonBuilder().setLenient().create();

				Retrofit kakaoPlaceInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
						.baseUrl(KAKAO_PLACE_JSON_URL).build();

				return kakaoPlaceInstance.create(Querys.class);
			}

			case SGIS_TRANSFORMATION: {
				OkHttpClient client = new OkHttpClient.Builder().connectTimeout(8, TimeUnit.SECONDS).build();
				Gson gson = new GsonBuilder().setLenient().create();

				Retrofit sgisTransformationInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
						.baseUrl(SGIS_TRANSFORMATION_SERVICE_URL).build();

				return sgisTransformationInstance.create(Querys.class);
			}

			case SGIS_FIGURE: {
				OkHttpClient client = new OkHttpClient.Builder().connectTimeout(8, TimeUnit.SECONDS).build();
				Gson gson = new GsonBuilder().setLenient().create();

				Retrofit sgisFigureInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
						.baseUrl(SGIS_FIGURE_SERVICE_URL).build();
				return sgisFigureInstance.create(Querys.class);
			}

			case SGIS_ADDRESS: {
				OkHttpClient client = new OkHttpClient.Builder().connectTimeout(8, TimeUnit.SECONDS).build();
				Gson gson = new GsonBuilder().setLenient().create();

				Retrofit sgisAddressInstance = new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create(gson))
						.baseUrl(SGIS_ADDRESS_SERVICE_URL).build();

				return sgisAddressInstance.create(Querys.class);
			}

			default:
				return null;

		}
	}

}
