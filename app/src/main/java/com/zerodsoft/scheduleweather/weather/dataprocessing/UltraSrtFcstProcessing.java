package com.zerodsoft.scheduleweather.weather.dataprocessing;

import android.content.Context;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
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
	private final WeatherDataDownloader weatherDataDownloader = new WeatherDataDownloader();

	public UltraSrtFcstProcessing(Context context, String LATITUDE, String LONGITUDE) {
		super(context, LATITUDE, LONGITUDE);
	}

	@Override
	public void getWeatherData(WeatherDataCallback<UltraSrtFcstResult> weatherDataCallback) {
		weatherDbRepository.getWeatherData(LATITUDE, LONGITUDE, WeatherDataDTO.ULTRA_SRT_FCST,
				new CarrierMessagingService.ResultCallback<WeatherDataDTO>() {
					@Override
					public void onReceiveResult(@NonNull WeatherDataDTO ultraSrtFcstWeatherDataDTO) throws RemoteException {
						if (ultraSrtFcstWeatherDataDTO == null) {
							refresh(weatherDataCallback);
						} else {
							Gson gson = new Gson();
							UltraSrtFcstRoot ultraSrtFcstRoot = gson.fromJson(ultraSrtFcstWeatherDataDTO.getJson(), UltraSrtFcstRoot.class);

							UltraSrtFcstResult ultraSrtFcstResult = new UltraSrtFcstResult();
							ultraSrtFcstResult.setUltraSrtFcstFinalDataList(ultraSrtFcstRoot.getResponse().getBody().getItems(), new Date(Long.parseLong(ultraSrtFcstWeatherDataDTO.getDownloadedDate())));

							weatherDataCallback.isSuccessful(ultraSrtFcstResult);
						}
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
								new CarrierMessagingService.ResultCallback<Boolean>() {
									@Override
									public void onReceiveResult(@NonNull Boolean isContains) throws RemoteException {
										if (isContains) {
											weatherDbRepository.update(LATITUDE, LONGITUDE, WeatherDataDTO.ULTRA_SRT_FCST, result.toString()
													, ultraSrtFcstWeatherDataDTO.getDownloadedDate(), new CarrierMessagingService.ResultCallback<Boolean>() {
														@Override
														public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException {
															UltraSrtFcstResult ultraSrtFcstResult = new UltraSrtFcstResult();
															ultraSrtFcstResult.setUltraSrtFcstFinalDataList(ultraSrtFcstRoot.getResponse().getBody().getItems(), new Date(Long.parseLong(ultraSrtFcstWeatherDataDTO.getDownloadedDate())));

															weatherDataCallback.isSuccessful(ultraSrtFcstResult);
														}
													});
										} else {
											weatherDbRepository.insert(ultraSrtFcstWeatherDataDTO, new CarrierMessagingService.ResultCallback<WeatherDataDTO>() {
												@Override
												public void onReceiveResult(@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException {
													UltraSrtFcstResult ultraSrtFcstResult = new UltraSrtFcstResult();
													ultraSrtFcstResult.setUltraSrtFcstFinalDataList(ultraSrtFcstRoot.getResponse().getBody().getItems(), new Date(Long.parseLong(ultraSrtFcstWeatherDataDTO.getDownloadedDate())));

													weatherDataCallback.isSuccessful(ultraSrtFcstResult);
												}
											});
										}
									}
								});
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
