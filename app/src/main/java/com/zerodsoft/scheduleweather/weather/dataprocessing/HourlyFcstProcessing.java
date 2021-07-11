package com.zerodsoft.scheduleweather.weather.dataprocessing;

import android.content.Context;

import com.google.gson.Gson;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.UltraSrtFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.commons.Header;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse.UltraSrtFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstRoot;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataHeaderChecker;
import com.zerodsoft.scheduleweather.weather.hourlyfcst.HourlyFcstResult;
import com.zerodsoft.scheduleweather.weather.hourlyfcst.HourlyFcstRoot;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDataDownloader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HourlyFcstProcessing extends WeatherDataProcessing<HourlyFcstResult> {
	private final WeatherDataDownloader weatherDataDownloader = WeatherDataDownloader.getInstance();

	public HourlyFcstProcessing(Context context, String LATITUDE, String LONGITUDE) {
		super(context, LATITUDE, LONGITUDE);
	}

	@Override
	public void getWeatherData(WeatherDataCallback<HourlyFcstResult> weatherDataCallback) {
		weatherDbRepository.getWeatherData(LATITUDE, LONGITUDE, WeatherDataDTO.ULTRA_SRT_FCST, new DbQueryCallback<WeatherDataDTO>() {
			@Override
			public void onResultSuccessful(WeatherDataDTO ultraSrtFcstWeatherDto) {
				weatherDbRepository.getWeatherData(LATITUDE, LONGITUDE, WeatherDataDTO.VILAGE_FCST, new DbQueryCallback<WeatherDataDTO>() {
					@Override
					public void onResultSuccessful(WeatherDataDTO vilageFcstWeatherDto) {
						VilageFcstRoot vilageFcstRoot = new Gson().fromJson(vilageFcstWeatherDto.getJson(), VilageFcstRoot.class);
						UltraSrtFcstRoot ultraSrtFcstRoot = new Gson().fromJson(ultraSrtFcstWeatherDto.getJson(), UltraSrtFcstRoot.class);

						HourlyFcstResult hourlyFcstResult = new HourlyFcstResult();
						hourlyFcstResult.setHourlyFcstFinalDataList(vilageFcstRoot.getResponse().getBody().getItems(),
								ultraSrtFcstRoot.getResponse().getBody().getItems(), new Date(Long.parseLong(ultraSrtFcstWeatherDto.getDownloadedDate())));

						weatherDataCallback.isSuccessful(hourlyFcstResult);
					}

					@Override
					public void onResultNoData() {

					}
				});
			}

			@Override
			public void onResultNoData() {
				refresh(weatherDataCallback);
			}
		});
	}


	@Override
	public void refresh(WeatherDataCallback<HourlyFcstResult> weatherDataCallback) {
		final VilageFcstParameter vilageFcstParameter = new VilageFcstParameter();
		vilageFcstParameter.setNx(LONGITUDE).setNy(LATITUDE).setNumOfRows("1000").setPageNo("1");

		final UltraSrtFcstParameter ultraSrtFcstParameter = new UltraSrtFcstParameter();
		ultraSrtFcstParameter.setNx(LONGITUDE).setNy(LATITUDE).setNumOfRows("400").setPageNo("1");

		Calendar calendar = Calendar.getInstance();
		weatherDataDownloader.getHourlyFcstData(vilageFcstParameter, ultraSrtFcstParameter, calendar, new JsonDownloader<HourlyFcstRoot>() {
			@Override
			public void onResponseSuccessful(HourlyFcstRoot hourlyFcstRoot) {
				Gson gson = new Gson();
				UltraSrtFcstRoot ultraSrtFcstRoot = gson.fromJson(hourlyFcstRoot.getUltraSrtFcst().toString(), UltraSrtFcstRoot.class);
				VilageFcstRoot vilageFcstRoot = gson.fromJson(hourlyFcstRoot.getVilageFcst().toString(), VilageFcstRoot.class);

				Header[] headers = {ultraSrtFcstRoot.getResponse().getHeader(), vilageFcstRoot.getResponse().getHeader()};
				List<String> exceptionMsgList = new ArrayList<>();
				for (Header header : headers) {
					new WeatherDataHeaderChecker() {
						@Override
						public void isSuccessful() {

						}

						@Override
						public void isFailure(Exception e) {
							exceptionMsgList.add(e.getMessage());
						}
					}.processResult(header);
				}

				if (exceptionMsgList.isEmpty()) {
					Date downloadedDate = new Date(System.currentTimeMillis());

					WeatherDataDTO ultraSrtFcstWeatherDataDTO = new WeatherDataDTO();
					ultraSrtFcstWeatherDataDTO.setLatitude(LATITUDE);
					ultraSrtFcstWeatherDataDTO.setLongitude(LONGITUDE);
					ultraSrtFcstWeatherDataDTO.setDataType(WeatherDataDTO.ULTRA_SRT_FCST);
					ultraSrtFcstWeatherDataDTO.setJson(hourlyFcstRoot.getUltraSrtFcst().toString());
					ultraSrtFcstWeatherDataDTO.setDownloadedDate(String.valueOf(downloadedDate.getTime()));

					WeatherDataDTO vilageFcstWeatherDataDTO = new WeatherDataDTO();
					vilageFcstWeatherDataDTO.setLatitude(LATITUDE);
					vilageFcstWeatherDataDTO.setLongitude(LONGITUDE);
					vilageFcstWeatherDataDTO.setDataType(WeatherDataDTO.VILAGE_FCST);
					vilageFcstWeatherDataDTO.setJson(hourlyFcstRoot.getUltraSrtFcst().toString());
					vilageFcstWeatherDataDTO.setDownloadedDate(String.valueOf(downloadedDate.getTime()));

					weatherDbRepository.contains(LATITUDE, LONGITUDE, WeatherDataDTO.VILAGE_FCST,
							new DbQueryCallback<Boolean>() {
								@Override
								public void onResultSuccessful(Boolean isContains) {
									if (isContains) {
										weatherDbRepository.update(LATITUDE, LONGITUDE, WeatherDataDTO.VILAGE_FCST
												, vilageFcstWeatherDataDTO.getJson(), vilageFcstWeatherDataDTO.getDownloadedDate(), null);
									} else {
										weatherDbRepository.insert(vilageFcstWeatherDataDTO, null);
									}
								}

								@Override
								public void onResultNoData() {

								}
							});

					weatherDbRepository.contains(LATITUDE, LONGITUDE, WeatherDataDTO.ULTRA_SRT_FCST,
							new DbQueryCallback<Boolean>() {
								@Override
								public void onResultSuccessful(Boolean isContains) {
									if (isContains) {
										weatherDbRepository.update(LATITUDE, LONGITUDE, WeatherDataDTO.ULTRA_SRT_FCST
												, ultraSrtFcstWeatherDataDTO.getJson(), ultraSrtFcstWeatherDataDTO.getDownloadedDate(), null);
									} else {
										weatherDbRepository.insert(ultraSrtFcstWeatherDataDTO, null);
									}
								}

								@Override
								public void onResultNoData() {

								}
							});

					HourlyFcstResult hourlyFcstResult = new HourlyFcstResult();
					hourlyFcstResult.setHourlyFcstFinalDataList(vilageFcstRoot.getResponse().getBody().getItems(),
							ultraSrtFcstRoot.getResponse().getBody().getItems(), downloadedDate);

					weatherDataCallback.isSuccessful(hourlyFcstResult);
				} else {
					weatherDataCallback.isFailure(new Exception(exceptionMsgList.toString()));
				}
			}

			@Override
			public void onResponseFailed(Exception e) {
				weatherDataCallback.isFailure(e);
			}
		});

	}
}
