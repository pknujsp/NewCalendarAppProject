package com.zerodsoft.scheduleweather.weather.dataprocessing;

import android.content.Context;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.VilageFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.vilagefcstresponse.VilageFcstRoot;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataHeaderChecker;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDataDownloader;
import com.zerodsoft.scheduleweather.weather.vilagefcst.VilageFcstResult;

import java.util.Calendar;
import java.util.Date;

public class VilageFcstProcessing extends WeatherDataProcessing<VilageFcstResult> {
	private final WeatherDataDownloader weatherDataDownloader = WeatherDataDownloader.getInstance();

	public VilageFcstProcessing(Context context, String LATITUDE, String LONGITUDE) {
		super(context, LATITUDE, LONGITUDE);
	}

	@Override
	public void getWeatherData(WeatherDataCallback<VilageFcstResult> weatherDataCallback) {
		weatherDbRepository.getWeatherData(LATITUDE, LONGITUDE, WeatherDataDTO.VILAGE_FCST,
				new DbQueryCallback<WeatherDataDTO>() {
					@Override
					public void onResultSuccessful(WeatherDataDTO vilageFcstResultDto) {
						Gson gson = new Gson();
						VilageFcstRoot vilageFcstRoot = gson.fromJson(vilageFcstResultDto.getJson(), VilageFcstRoot.class);
						VilageFcstResult vilageFcstResult = new VilageFcstResult();
						vilageFcstResult.setVilageFcstDataList(vilageFcstRoot.getResponse().getBody().getItems(), new Date(Long.parseLong(vilageFcstResultDto.getDownloadedDate())));

						weatherDataCallback.isSuccessful(vilageFcstResult);
					}

					@Override
					public void onResultNoData() {
						refresh(weatherDataCallback);
					}
				});
	}

	@Override
	public void refresh(WeatherDataCallback<VilageFcstResult> weatherDataCallback) {
		VilageFcstParameter vilageFcstParameter = new VilageFcstParameter();
		vilageFcstParameter.setNx(LONGITUDE).setNy(LATITUDE).setNumOfRows("300").setPageNo("1");

		Calendar calendar = Calendar.getInstance();
		weatherDataDownloader.getVilageFcstData(vilageFcstParameter, calendar, new JsonDownloader<JsonObject>() {
			@Override
			public void onResponseSuccessful(JsonObject result) {
				Gson gson = new Gson();
				VilageFcstRoot vilageFcstRoot = gson.fromJson(result.toString(), VilageFcstRoot.class);

				new WeatherDataHeaderChecker() {
					@Override
					public void isSuccessful() {
						Date downloadedDate = new Date(System.currentTimeMillis());

						WeatherDataDTO vilageFcstWeatherDataDTO = new WeatherDataDTO();
						vilageFcstWeatherDataDTO.setLatitude(LATITUDE);
						vilageFcstWeatherDataDTO.setLongitude(LONGITUDE);
						vilageFcstWeatherDataDTO.setDataType(WeatherDataDTO.VILAGE_FCST);
						vilageFcstWeatherDataDTO.setJson(result.toString());
						vilageFcstWeatherDataDTO.setDownloadedDate(String.valueOf(downloadedDate.getTime()));

						weatherDbRepository.contains(LATITUDE, LONGITUDE, WeatherDataDTO.VILAGE_FCST,
								new DbQueryCallback<Boolean>() {
									@Override
									public void onResultSuccessful(Boolean isContains) {
										if (isContains) {
											weatherDbRepository.update(LATITUDE, LONGITUDE, WeatherDataDTO.VILAGE_FCST, result.toString()
													, vilageFcstWeatherDataDTO.getDownloadedDate(), new DbQueryCallback<Boolean>() {
														@Override
														public void onResultSuccessful(Boolean resultDto) {

														}

														@Override
														public void onResultNoData() {

														}
													});
										} else {
											weatherDbRepository.insert(vilageFcstWeatherDataDTO, new DbQueryCallback<WeatherDataDTO>() {
												@Override
												public void onResultSuccessful(WeatherDataDTO resultDto) {

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
						VilageFcstResult vilageFcstResult = new VilageFcstResult();
						vilageFcstResult.setVilageFcstDataList(vilageFcstRoot.getResponse().getBody().getItems(), downloadedDate);

						weatherDataCallback.isSuccessful(vilageFcstResult);
					}

					@Override
					public void isFailure(Exception e) {
						weatherDataCallback.isFailure(e);
					}
				}.processResult(vilageFcstRoot.getResponse().getHeader());

			}

			@Override
			public void onResponseFailed(Exception e) {
				weatherDataCallback.isFailure(e);
			}
		});
	}
}
