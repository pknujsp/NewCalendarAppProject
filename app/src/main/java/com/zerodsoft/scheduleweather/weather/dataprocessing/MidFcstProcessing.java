package com.zerodsoft.scheduleweather.weather.dataprocessing;

import android.content.Context;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidLandFcstParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MidTaParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midlandfcstresponse.MidLandFcstRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midtaresponse.MidTaRoot;
import com.zerodsoft.scheduleweather.room.dto.WeatherAreaCodeDTO;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.mid.MidFcstResult;
import com.zerodsoft.scheduleweather.weather.mid.MidFcstRoot;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDataDownloader;

import java.util.Calendar;
import java.util.Date;

public class MidFcstProcessing extends WeatherDataProcessing<MidFcstResult> {
	private WeatherDataDownloader weatherDataDownloader = new WeatherDataDownloader();
	private WeatherAreaCodeDTO weatherAreaCode;

	public MidFcstProcessing(Context context, String LATITUDE, String LONGITUDE, WeatherAreaCodeDTO weatherAreaCodeDTO) {
		super(context, LATITUDE, LONGITUDE);
		this.weatherAreaCode = weatherAreaCodeDTO;
	}

	@Override
	public void getWeatherData(WeatherDataCallback<MidFcstResult> weatherDataCallback) {
		weatherDbRepository.getWeatherData(LATITUDE, LONGITUDE, WeatherDataDTO.MID_LAND_FCST,
				new CarrierMessagingService.ResultCallback<WeatherDataDTO>() {
					@Override
					public void onReceiveResult(@NonNull WeatherDataDTO midLandFcstWeatherDataDTO) throws RemoteException {
						if (midLandFcstWeatherDataDTO == null) {
							refresh(weatherDataCallback);
						} else {
							weatherDbRepository.getWeatherData(LATITUDE, LONGITUDE, WeatherDataDTO.MID_TA,
									new CarrierMessagingService.ResultCallback<WeatherDataDTO>() {
										@Override
										public void onReceiveResult(@NonNull WeatherDataDTO midTaWeatherDataDTO) throws RemoteException {
											Gson gson = new Gson();
											MidLandFcstRoot midLandFcstRoot = gson.fromJson(midLandFcstWeatherDataDTO.getJson(), MidLandFcstRoot.class);
											MidTaRoot midTaRoot = gson.fromJson(midTaWeatherDataDTO.getJson(), MidTaRoot.class);

											MidFcstResult midFcstResult = new MidFcstResult();
											midFcstResult.setMidFcstDataList(midLandFcstRoot.getResponse().getBody().getItems()
													, midTaRoot.getResponse().getBody().getItems(), new Date(Long.parseLong(midTaWeatherDataDTO.getDownloadedDate())));

											weatherDataCallback.isSuccessful(midFcstResult);
										}
									});
						}
					}
				});

	}

	@Override
	public void refresh(WeatherDataCallback<MidFcstResult> weatherDataCallback) {
		MidLandFcstParameter midLandFcstParameter = new MidLandFcstParameter();
		MidTaParameter midTaParameter = new MidTaParameter();

		midLandFcstParameter.setNumOfRows("300").setPageNo("1").setRegId(weatherAreaCode.getMidLandFcstCode());
		midTaParameter.setNumOfRows("300").setPageNo("1").setRegId(weatherAreaCode.getMidTaCode());

		Calendar calendar = Calendar.getInstance();
		weatherDataDownloader.getMidFcstData(midLandFcstParameter, midTaParameter, calendar, new JsonDownloader<MidFcstRoot>() {
			@Override
			public void onResponseSuccessful(MidFcstRoot midFcstRoot) {
				Gson gson = new Gson();
				MidLandFcstRoot midLandFcstRoot = gson.fromJson(midFcstRoot.getMidLandFcst().toString(), MidLandFcstRoot.class);
				MidTaRoot midTaRoot = gson.fromJson(midFcstRoot.getMidTa().toString(), MidTaRoot.class);

				Date downloadedDate = new Date(System.currentTimeMillis());

				WeatherDataDTO midLandFcstWeatherDataDTO = new WeatherDataDTO();
				midLandFcstWeatherDataDTO.setLatitude(LATITUDE);
				midLandFcstWeatherDataDTO.setLongitude(LONGITUDE);
				midLandFcstWeatherDataDTO.setDataType(WeatherDataDTO.MID_LAND_FCST);
				midLandFcstWeatherDataDTO.setJson(midFcstRoot.getMidLandFcst().toString());
				midLandFcstWeatherDataDTO.setDownloadedDate(String.valueOf(downloadedDate.getTime()));

				WeatherDataDTO midTaWeatherDataDTO = new WeatherDataDTO();
				midTaWeatherDataDTO.setLatitude(LATITUDE);
				midTaWeatherDataDTO.setLongitude(LONGITUDE);
				midTaWeatherDataDTO.setDataType(WeatherDataDTO.MID_TA);
				midTaWeatherDataDTO.setJson(midFcstRoot.getMidTa().toString());
				midTaWeatherDataDTO.setDownloadedDate(String.valueOf(downloadedDate.getTime()));

				weatherDbRepository.contains(LATITUDE, LONGITUDE, WeatherDataDTO.MID_LAND_FCST,
						new CarrierMessagingService.ResultCallback<Boolean>() {
							@Override
							public void onReceiveResult(@NonNull Boolean isContains) throws RemoteException {
								if (isContains) {
									weatherDbRepository.update(LATITUDE, LONGITUDE, WeatherDataDTO.MID_LAND_FCST
											, midLandFcstWeatherDataDTO.getJson(), midLandFcstWeatherDataDTO.getDownloadedDate(), new CarrierMessagingService.ResultCallback<Boolean>() {
												@Override
												public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException {

												}
											});
								} else {
									weatherDbRepository.insert(midLandFcstWeatherDataDTO, new CarrierMessagingService.ResultCallback<WeatherDataDTO>() {
										@Override
										public void onReceiveResult(@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException {

										}
									});
								}
							}
						});

				weatherDbRepository.contains(LATITUDE, LONGITUDE, WeatherDataDTO.MID_TA,
						new CarrierMessagingService.ResultCallback<Boolean>() {
							@Override
							public void onReceiveResult(@NonNull Boolean isContains) throws RemoteException {
								if (isContains) {
									weatherDbRepository.update(LATITUDE, LONGITUDE, WeatherDataDTO.MID_TA
											, midTaWeatherDataDTO.getJson(), midTaWeatherDataDTO.getDownloadedDate(), new CarrierMessagingService.ResultCallback<Boolean>() {
												@Override
												public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException {

												}
											});
								} else {
									weatherDbRepository.insert(midTaWeatherDataDTO, new CarrierMessagingService.ResultCallback<WeatherDataDTO>() {
										@Override
										public void onReceiveResult(@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException {

										}
									});
								}
							}
						});

				MidFcstResult midFcstResult = new MidFcstResult();
				midFcstResult.setMidFcstDataList(midLandFcstRoot.getResponse().getBody().getItems()
						, midTaRoot.getResponse().getBody().getItems(), downloadedDate);

				weatherDataCallback.isSuccessful(midFcstResult);
			}

			@Override
			public void onResponseFailed(Exception e) {
				weatherDataCallback.isFailure(e);
			}
		});
	}
}
