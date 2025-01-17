package com.zerodsoft.calendarplatform.weather.repository;

import com.google.gson.JsonObject;
import com.zerodsoft.calendarplatform.common.classes.JsonDownloader;
import com.zerodsoft.calendarplatform.common.classes.RetrofitCallListManager;
import com.zerodsoft.calendarplatform.retrofit.HttpCommunicationClient;
import com.zerodsoft.calendarplatform.retrofit.Querys;
import com.zerodsoft.calendarplatform.retrofit.paremeters.MidLandFcstParameter;
import com.zerodsoft.calendarplatform.retrofit.paremeters.MidTaParameter;
import com.zerodsoft.calendarplatform.retrofit.paremeters.UltraSrtFcstParameter;
import com.zerodsoft.calendarplatform.retrofit.paremeters.UltraSrtNcstParameter;
import com.zerodsoft.calendarplatform.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.calendarplatform.utility.ClockUtil;
import com.zerodsoft.calendarplatform.weather.hourlyfcst.HourlyFcstRoot;
import com.zerodsoft.calendarplatform.weather.mid.MidFcstRoot;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDataDownloader implements RetrofitCallListManager.CallManagerListener {
	public static final String ULTRA_SRT_NCST = "ULTRA_SRT_NCST";
	public static final String ULTRA_SRT_FCST = "ULTRA_SRT_FCST";
	public static final String VILAGE_FCST = "VILAGE_FCST";
	public static final String MID_LAND_FCST = "MID_LAND_FCST";
	public static final String MID_TA_FCST = "MID_TA_FCST";

	private static RetrofitCallListManager retrofitCallListManager = new RetrofitCallListManager();
	private static WeatherDataDownloader instance = new WeatherDataDownloader();

	public static WeatherDataDownloader getInstance() {
		return instance;
	}

	public static void close() {
		retrofitCallListManager.clear();
	}

	public WeatherDataDownloader() {
	}

	/**
	 * 초단기 실황
	 *
	 * @param parameter
	 */
	public void getUltraSrtNcstData(UltraSrtNcstParameter parameter, Calendar calendar, JsonDownloader<JsonObject> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.VILAGE_FCST);
		//basetime설정

		if (calendar.get(Calendar.MINUTE) < 40) {
			calendar.add(Calendar.HOUR_OF_DAY, -1);
		}
		parameter.setBaseDate(ClockUtil.yyyyMMdd.format(calendar.getTime()));
		parameter.setBaseTime(ClockUtil.HH.format(calendar.getTime()) + "00");

		Call<JsonObject> call = querys.getUltraSrtNcstDataStr(parameter.getMap());
		retrofitCallListManager.add(call);

		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				callback.processResult(response);
				retrofitCallListManager.remove(call);
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {
				callback.processResult(t);
				retrofitCallListManager.remove(call);
			}
		});

	}

	/**
	 * 초단기예보
	 *
	 * @param parameter
	 */
	public void getUltraSrtFcstData(UltraSrtFcstParameter parameter, Calendar calendar, JsonDownloader<JsonObject> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.VILAGE_FCST);
		//basetime설정
		if (calendar.get(Calendar.MINUTE) < 45) {
			calendar.add(Calendar.HOUR_OF_DAY, -1);
		}
		parameter.setBaseDate(ClockUtil.yyyyMMdd.format(calendar.getTime()));
		parameter.setBaseTime(ClockUtil.HH.format(calendar.getTime()) + "30");

		Call<JsonObject> call = querys.getUltraSrtFcstDataStr(parameter.getMap());
		retrofitCallListManager.add(call);

		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				callback.processResult(response);
				retrofitCallListManager.remove(call);
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {
				callback.processResult(t);
				retrofitCallListManager.remove(call);
			}
		});
	}

	/**
	 * 동네예보
	 * <p>
	 * - Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회)
	 * - API 제공 시간(~이후) : 02:10, 05:10, 08:10, 11:10, 14:10, 17:10, 20:10, 23:10
	 */
	public void getVilageFcstData(VilageFcstParameter parameter, Calendar calendar, JsonDownloader<JsonObject> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.VILAGE_FCST);
		//basetime설정
		final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		final int currentMinute = calendar.get(Calendar.MINUTE);
		int i = currentHour >= 0 && currentHour <= 2 ? 7 : currentHour / 3 - 1;
		int baseHour = 0;

		if (currentMinute > 10 && (currentHour - 2) % 3 == 0) {
			// ex)1411인 경우
			baseHour = 3 * ((currentHour - 2) / 3) + 2;
			i = 0;
		} else {
			baseHour = 3 * i + 2;
		}

		if (i == 7) {
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
		} else {
			calendar.set(Calendar.HOUR_OF_DAY, baseHour);
		}

		parameter.setBaseDate(ClockUtil.yyyyMMdd.format(calendar.getTime()));
		parameter.setBaseTime(ClockUtil.HH.format(calendar.getTime()) + "00");

		Call<JsonObject> call = querys.getVilageFcstDataStr(parameter.getMap());
		retrofitCallListManager.add(call);

		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				callback.processResult(response);
				retrofitCallListManager.remove(call);
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {
				callback.processResult(t);
				retrofitCallListManager.remove(call);
			}
		});

	}

	public void getHourlyFcstData(VilageFcstParameter vilageFcstParameter, UltraSrtFcstParameter ultraSrtFcstParameter, Calendar calendar,
	                              JsonDownloader<HourlyFcstRoot> callback) {
		HourlyFcstRoot hourlyFcstRoot = new HourlyFcstRoot();

		getVilageFcstData(vilageFcstParameter, (Calendar) calendar.clone(), new JsonDownloader<JsonObject>() {
			@Override
			public void onResponseSuccessful(JsonObject result) {
				hourlyFcstRoot.setVilageFcst(result);
				checkHourlyFcstDataDownload(hourlyFcstRoot, callback);
			}

			@Override
			public void onResponseFailed(Exception e) {
				hourlyFcstRoot.setException(e);
				checkHourlyFcstDataDownload(hourlyFcstRoot, callback);
			}
		});

		getUltraSrtFcstData(ultraSrtFcstParameter, (Calendar) calendar.clone(), new JsonDownloader<JsonObject>() {
			@Override
			public void onResponseSuccessful(JsonObject result) {
				hourlyFcstRoot.setUltraSrtFcst(result);
				checkHourlyFcstDataDownload(hourlyFcstRoot, callback);
			}

			@Override
			public void onResponseFailed(Exception e) {
				hourlyFcstRoot.setException(e);
				checkHourlyFcstDataDownload(hourlyFcstRoot, callback);
			}
		});
	}

	/**
	 * 중기육상예보
	 *
	 * @param parameter
	 */
	public void getMidLandFcstData(MidLandFcstParameter parameter, JsonDownloader<JsonObject> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.MID_FCST);

		Call<JsonObject> call = querys.getMidLandFcstDataStr(parameter.getMap());
		retrofitCallListManager.add(call);

		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				callback.processResult(response);
				retrofitCallListManager.remove(call);
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {
				callback.processResult(t);
				retrofitCallListManager.remove(call);
			}
		});

	}

	/**
	 * 중기기온조회
	 *
	 * @param parameter
	 */
	public void getMidTaData(MidTaParameter parameter, JsonDownloader<JsonObject> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.MID_FCST);

		Call<JsonObject> call = querys.getMidTaDataStr(parameter.getMap());
		retrofitCallListManager.add(call);

		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				callback.processResult(response);
				retrofitCallListManager.remove(call);
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {
				callback.processResult(t);
				retrofitCallListManager.remove(call);
			}
		});
	}

	public void getMidFcstData(MidLandFcstParameter midLandFcstParameter, MidTaParameter midTaParameter,
	                           JsonDownloader<MidFcstRoot> callback) {
		MidFcstRoot midFcstRoot = new MidFcstRoot();

		getMidLandFcstData(midLandFcstParameter, new JsonDownloader<JsonObject>() {
			@Override
			public void onResponseSuccessful(JsonObject result) {
				midFcstRoot.setMidLandFcst(result);
				checkMidFcstDataDownload(midFcstRoot, callback);
			}

			@Override
			public void onResponseFailed(Exception e) {
				midFcstRoot.setException(e);
				checkMidFcstDataDownload(midFcstRoot, callback);
			}
		});

		getMidTaData(midTaParameter, new JsonDownloader<JsonObject>() {
			@Override
			public void onResponseSuccessful(JsonObject result) {
				midFcstRoot.setMidTa(result);
				checkMidFcstDataDownload(midFcstRoot, callback);
			}

			@Override
			public void onResponseFailed(Exception e) {
				midFcstRoot.setException(e);
				checkMidFcstDataDownload(midFcstRoot, callback);
			}
		});
	}

	synchronized private void checkMidFcstDataDownload(MidFcstRoot midFcstRoot, JsonDownloader<MidFcstRoot> callback) {
		if (midFcstRoot.getCount() == 2) {
			if (midFcstRoot.getException() == null) {
				callback.onResponseSuccessful(midFcstRoot);
			} else {
				callback.onResponseFailed(midFcstRoot.getException());
			}
		}
	}

	synchronized private void checkHourlyFcstDataDownload(HourlyFcstRoot hourlyFcstRoot, JsonDownloader<HourlyFcstRoot> callback) {
		if (hourlyFcstRoot.getCount() == 2) {
			if (hourlyFcstRoot.getException() == null) {
				callback.onResponseSuccessful(hourlyFcstRoot);
			} else {
				callback.onResponseFailed(hourlyFcstRoot.getException());
			}
		}
	}

	@Override
	public void clear() {
		retrofitCallListManager.clear();
	}
}
