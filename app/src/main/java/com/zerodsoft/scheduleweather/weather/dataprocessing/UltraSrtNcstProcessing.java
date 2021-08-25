package com.zerodsoft.scheduleweather.weather.dataprocessing;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.UltraSrtNcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse.UltraSrtNcstRoot;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.utility.ClockUtil;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataHeaderChecker;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDataDownloader;
import com.zerodsoft.scheduleweather.weather.ultrasrtncst.UltraSrtNcstResult;

import java.util.Calendar;
import java.util.Date;

public class UltraSrtNcstProcessing extends WeatherDataProcessing<UltraSrtNcstResult> {
	private final WeatherDataDownloader weatherDataDownloader = WeatherDataDownloader.getInstance();

	public UltraSrtNcstProcessing(Context context, String LATITUDE, String LONGITUDE) {
		super(context, LATITUDE, LONGITUDE);
	}

	@Override
	public void getWeatherData(WeatherDataCallback<UltraSrtNcstResult> weatherDataCallback) {
		weatherDbRepository.getWeatherData(LATITUDE, LONGITUDE, WeatherDataDTO.ULTRA_SRT_NCST,
				new DbQueryCallback<WeatherDataDTO>() {
					@Override
					public void onResultSuccessful(WeatherDataDTO ultraSrtNcstResultDto) {
						Gson gson = new Gson();
						UltraSrtNcstRoot ultraSrtNcstRoot = gson.fromJson(ultraSrtNcstResultDto.getJson(), UltraSrtNcstRoot.class);
						UltraSrtNcstResult ultraSrtNcstResult = new UltraSrtNcstResult();
						ultraSrtNcstResult.setUltraSrtNcstFinalData(ultraSrtNcstRoot.getResponse().getBody().getItems(),
								new Date(Long.parseLong(ultraSrtNcstResultDto.getDownloadedDate())));

						weatherDataCallback.isSuccessful(ultraSrtNcstResult);

					}

					@Override
					public void onResultNoData() {
						refresh(weatherDataCallback);
					}
				});
	}

	@Override
	public void refresh(WeatherDataCallback<UltraSrtNcstResult> weatherDataCallback) {
		UltraSrtNcstParameter ultraSrtNcstParameter = new UltraSrtNcstParameter();
		ultraSrtNcstParameter.setNx(LONGITUDE).setNy(LATITUDE).setNumOfRows("250").setPageNo("1");

		Calendar calendar = Calendar.getInstance(ClockUtil.TIME_ZONE);
		weatherDataDownloader.getUltraSrtNcstData(ultraSrtNcstParameter, calendar, new JsonDownloader<JsonObject>() {
			@Override
			public void onResponseSuccessful(JsonObject result) {
				Gson gson = new Gson();
				UltraSrtNcstRoot ultraSrtNcstRoot = gson.fromJson(result.toString(), UltraSrtNcstRoot.class);

				new WeatherDataHeaderChecker() {
					@Override
					public void isSuccessful() {
						Date downloadedDate = new Date(System.currentTimeMillis());
						final String ultraSrtNcstBaseDateTimeStr =
								ultraSrtNcstRoot.getResponse().getBody().getItems().getItem().get(0).getBaseDate()
										+ ultraSrtNcstRoot.getResponse().getBody().getItems().getItem().get(0).getBaseTime();

						calendar.set(Calendar.SECOND, 0);

						int year = Integer.parseInt(ultraSrtNcstBaseDateTimeStr.substring(0, 4));
						int month = Integer.parseInt(ultraSrtNcstBaseDateTimeStr.substring(4, 6));
						int day = Integer.parseInt(ultraSrtNcstBaseDateTimeStr.substring(6, 8));
						int hour = Integer.parseInt(ultraSrtNcstBaseDateTimeStr.substring(8, 10));
						int minute = Integer.parseInt(ultraSrtNcstBaseDateTimeStr.substring(10));

						calendar.set(Calendar.YEAR, year);
						calendar.set(Calendar.MONTH, month - 1);
						calendar.set(Calendar.DAY_OF_MONTH, day);
						calendar.set(Calendar.HOUR_OF_DAY, hour);
						calendar.set(Calendar.MINUTE, minute);

						WeatherDataDTO ultraSrtNcstWeatherDataDTO = new WeatherDataDTO();
						ultraSrtNcstWeatherDataDTO.setLatitude(LATITUDE);
						ultraSrtNcstWeatherDataDTO.setLongitude(LONGITUDE);
						ultraSrtNcstWeatherDataDTO.setDataType(WeatherDataDTO.ULTRA_SRT_NCST);
						ultraSrtNcstWeatherDataDTO.setJson(result.toString());
						ultraSrtNcstWeatherDataDTO.setDownloadedDate(String.valueOf(downloadedDate.getTime()));
						ultraSrtNcstWeatherDataDTO.setBaseDateTime(String.valueOf(calendar.getTimeInMillis()));

						weatherDbRepository.contains(LATITUDE, LONGITUDE, WeatherDataDTO.ULTRA_SRT_NCST,
								new DbQueryCallback<Boolean>() {
									@Override
									public void onResultSuccessful(Boolean isContains) {
										if (isContains) {
											weatherDbRepository.update(LATITUDE, LONGITUDE, WeatherDataDTO.ULTRA_SRT_NCST, result.toString()
													, ultraSrtNcstWeatherDataDTO.getDownloadedDate(), ultraSrtNcstWeatherDataDTO.getBaseDateTime(),
													new DbQueryCallback<Boolean>() {
														@Override
														public void onResultSuccessful(Boolean result) {

														}

														@Override
														public void onResultNoData() {

														}
													});
										} else {
											weatherDbRepository.insert(ultraSrtNcstWeatherDataDTO, new DbQueryCallback<WeatherDataDTO>() {
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

						UltraSrtNcstResult ultraSrtNcstResult = new UltraSrtNcstResult();
						ultraSrtNcstResult.setUltraSrtNcstFinalData(ultraSrtNcstRoot.getResponse().getBody().getItems(),
								downloadedDate);

						weatherDataCallback.isSuccessful(ultraSrtNcstResult);
					}

					@Override
					public void isFailure(Exception e) {
						weatherDataCallback.isFailure(e);
					}
				}.processResult(ultraSrtNcstRoot.getResponse().getHeader());

			}

			@Override
			public void onResponseFailed(Exception e) {
				weatherDataCallback.isFailure(e);
			}
		});
	}
}
