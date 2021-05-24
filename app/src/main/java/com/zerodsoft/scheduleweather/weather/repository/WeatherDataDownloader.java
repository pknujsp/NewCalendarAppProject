package com.zerodsoft.scheduleweather.weather.repository;

import android.os.Bundle;

import com.google.gson.JsonObject;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.classes.RetrofitCallListManager;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.RetrofitCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidLandFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidTaParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.UltraSrtFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.UltraSrtNcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.WeatherItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midlandfcstresponse.MidLandFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midlandfcstresponse.MidLandFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midtaresponse.MidTaItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midtaresponse.MidTaRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstItems;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstRoot;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.weather.mid.MidFcstRoot;

import java.util.Calendar;
import java.util.Stack;

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
	 *
	 * @param parameter
	 */
	public void getVilageFcstData(VilageFcstParameter parameter, Calendar calendar, JsonDownloader<JsonObject> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.VILAGE_FCST);
		//basetime설정
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int i = hour >= 0 && hour <= 2 ? 7 : hour / 3 - 1;
		int baseHour = 0;

		if (calendar.get(Calendar.MINUTE) > 10 && (hour - 2) % 3 == 0) {
			// ex)1411인 경우
			baseHour = 3 * ((hour - 2) / 3) + 2;
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

	/**
	 * 중기육상예보
	 *
	 * @param parameter
	 */
	public void getMidLandFcstData(MidLandFcstParameter parameter, Calendar calendar, JsonDownloader<JsonObject> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.MID_FCST);

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		if (hour >= 18 && minute >= 1) {
			parameter.setTmFc(ClockUtil.yyyyMMdd.format(calendar.getTime()) + "1800");
		} else if (hour >= 6 && minute >= 1) {
			parameter.setTmFc(ClockUtil.yyyyMMdd.format(calendar.getTime()) + "0600");
		} else {
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			parameter.setTmFc(ClockUtil.yyyyMMdd.format(calendar.getTime()) + "1800");
		}

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
	public void getMidTaData(MidTaParameter parameter, Calendar calendar, JsonDownloader<JsonObject> callback) {
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.MID_FCST);

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		if (hour >= 18 && minute >= 1) {
			parameter.setTmFc(ClockUtil.yyyyMMdd.format(calendar.getTime()) + "1800");
		} else if (hour >= 6 && minute >= 1) {
			parameter.setTmFc(ClockUtil.yyyyMMdd.format(calendar.getTime()) + "0600");
		} else {
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			parameter.setTmFc(ClockUtil.yyyyMMdd.format(calendar.getTime()) + "1800");
		}

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

	public void getMidFcstData(MidLandFcstParameter midLandFcstParameter, MidTaParameter midTaParameter, Calendar calendar, JsonDownloader<MidFcstRoot> callback) {
		MidFcstRoot midFcstRoot = new MidFcstRoot();

		getMidLandFcstData(midLandFcstParameter, (Calendar) calendar.clone(), new JsonDownloader<JsonObject>() {
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

		getMidTaData(midTaParameter, (Calendar) calendar.clone(), new JsonDownloader<JsonObject>() {
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

	@Override
	public void clear() {
		retrofitCallListManager.clear();
	}
}
