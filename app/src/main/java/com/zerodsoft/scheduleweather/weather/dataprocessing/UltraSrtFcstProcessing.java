package com.zerodsoft.scheduleweather.weather.dataprocessing;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.UltraSrtFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstRoot;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataHeaderChecker;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDataDownloader;
import com.zerodsoft.scheduleweather.weather.ultrasrtfcst.UltraSrtFcstResult;

import java.util.Calendar;
import java.util.Date;

public class UltraSrtFcstProcessing extends WeatherDataProcessing<UltraSrtFcstResult> {
	private final WeatherDataDownloader weatherDataDownloader = WeatherDataDownloader.getInstance();

	public UltraSrtFcstProcessing(Context context, String LATITUDE, String LONGITUDE) {
		super(context, LATITUDE, LONGITUDE);
	}

	@Override
	public void getWeatherData(WeatherDataCallback<UltraSrtFcstResult> weatherDataCallback) {
		weatherDbRepository.getWeatherData(LATITUDE, LONGITUDE, WeatherDataDTO.ULTRA_SRT_FCST,
				new DbQueryCallback<WeatherDataDTO>() {
					@Override
					public void onResultSuccessful(WeatherDataDTO ultraSrtFcstResultDto) {
						Gson gson = new Gson();
						UltraSrtFcstRoot ultraSrtFcstRoot = gson.fromJson(ultraSrtFcstResultDto.getJson(), UltraSrtFcstRoot.class);

						UltraSrtFcstResult ultraSrtFcstResult = new UltraSrtFcstResult();
						ultraSrtFcstResult.setUltraSrtFcstFinalDataList(ultraSrtFcstRoot.getResponse().getBody().getItems(), new Date(Long.parseLong(ultraSrtFcstResultDto.getDownloadedDate())));

						weatherDataCallback.isSuccessful(ultraSrtFcstResult);
					}

					@Override
					public void onResultNoData() {
						refresh(weatherDataCallback);
					}
				});
	}

	@Override
	public void refresh(WeatherDataCallback<UltraSrtFcstResult> weatherDataCallback) {
		UltraSrtFcstParameter ultraSrtFcstParameter = new UltraSrtFcstParameter();
		ultraSrtFcstParameter.setNx(LONGITUDE).setNy(LATITUDE).setNumOfRows("300").setPageNo("1");

		Calendar calendar = Calendar.getInstance();
		weatherDataDownloader.getUltraSrtFcstData(ultraSrtFcstParameter, calendar, new JsonDownloader<JsonObject>() {
			@Override
			public void onResponseSuccessful(JsonObject result) {
				Gson gson = new Gson();
				UltraSrtFcstRoot ultraSrtFcstRoot = gson.fromJson(result.toString(), UltraSrtFcstRoot.class);

				new WeatherDataHeaderChecker() {
					@Override
					public void isSuccessful() {
						Date downloadedDate = new Date(System.currentTimeMillis());

						WeatherDataDTO ultraSrtFcstWeatherDataDTO = new WeatherDataDTO();
						ultraSrtFcstWeatherDataDTO.setLatitude(LATITUDE);
						ultraSrtFcstWeatherDataDTO.setLongitude(LONGITUDE);
						ultraSrtFcstWeatherDataDTO.setDataType(WeatherDataDTO.ULTRA_SRT_FCST);
						ultraSrtFcstWeatherDataDTO.setJson(result.toString());
						ultraSrtFcstWeatherDataDTO.setDownloadedDate(String.valueOf(downloadedDate.getTime()));

						weatherDbRepository.contains(LATITUDE, LONGITUDE, WeatherDataDTO.ULTRA_SRT_FCST,
								new DbQueryCallback<Boolean>() {
									@Override
									public void onResultSuccessful(Boolean isContains) {
										if (isContains) {
											weatherDbRepository.update(LATITUDE, LONGITUDE, WeatherDataDTO.ULTRA_SRT_FCST, result.toString()
													, ultraSrtFcstWeatherDataDTO.getDownloadedDate(), ultraSrtFcstWeatherDataDTO.getBaseDateTime(), new DbQueryCallback<Boolean>() {
														@Override
														public void onResultSuccessful(Boolean result) {

														}

														@Override
														public void onResultNoData() {

														}
													});
										} else {
											weatherDbRepository.insert(ultraSrtFcstWeatherDataDTO, new DbQueryCallback<WeatherDataDTO>() {
												@Override
												public void onResultSuccessful(WeatherDataDTO result) {

												}

												@Override
												public void onResultNoData() {

												}
											});
										}
									}

									@Override
									public void onResultNoData() {

									}
								});

						UltraSrtFcstResult ultraSrtFcstResult = new UltraSrtFcstResult();
						ultraSrtFcstResult.setUltraSrtFcstFinalDataList(ultraSrtFcstRoot.getResponse().getBody().getItems(), new Date(Long.parseLong(ultraSrtFcstWeatherDataDTO.getDownloadedDate())));

						weatherDataCallback.isSuccessful(ultraSrtFcstResult);
					}

					@Override
					public void isFailure(Exception e) {
						weatherDataCallback.isFailure(e);
					}
				}.processResult(ultraSrtFcstRoot.getResponse().getHeader());

			}

			@Override
			public void onResponseFailed(Exception e) {
				weatherDataCallback.isFailure(e);
			}
		});
	}
}
