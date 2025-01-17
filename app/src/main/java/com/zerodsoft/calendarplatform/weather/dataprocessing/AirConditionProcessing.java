package com.zerodsoft.calendarplatform.weather.dataprocessing;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Utmk;
import com.zerodsoft.calendarplatform.common.classes.JsonDownloader;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.retrofit.paremeters.MsrstnAcctoRltmMesureDnstyParameter;
import com.zerodsoft.calendarplatform.retrofit.paremeters.NearbyMsrstnListParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyRoot;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListRoot;
import com.zerodsoft.calendarplatform.room.dto.WeatherDataDTO;
import com.zerodsoft.calendarplatform.utility.ClockUtil;
import com.zerodsoft.calendarplatform.weather.aircondition.airconditionbar.AirConditionResult;
import com.zerodsoft.calendarplatform.weather.common.WeatherDataCallback;
import com.zerodsoft.calendarplatform.weather.common.WeatherDataHeaderChecker;
import com.zerodsoft.calendarplatform.weather.repository.AirConditionDownloader;
import com.zerodsoft.calendarplatform.weather.repository.FindAirConditionStationDownloader;

import java.util.Calendar;
import java.util.Date;

public class AirConditionProcessing extends WeatherDataProcessing<AirConditionResult> {
	public AirConditionProcessing(Context context, String LATITUDE, String LONGITUDE) {
		super(context, LATITUDE, LONGITUDE);
	}

	private final AirConditionDownloader airConditionDownloader = AirConditionDownloader.getInstance();

	private final FindAirConditionStationDownloader findAirConditionStationDownloader = new FindAirConditionStationDownloader();

	private NearbyMsrstnListRoot nearbyMsrstnListRoot;

	@Override
	public void getWeatherData(WeatherDataCallback<AirConditionResult> weatherDataCallback) {
		weatherDbRepository.getWeatherData(LATITUDE, LONGITUDE, WeatherDataDTO.NEAR_BY_MSRSTN_LIST,
				new DbQueryCallback<WeatherDataDTO>() {
					@Override
					public void onResultSuccessful(WeatherDataDTO resultNearByMsrstnListDto) {
						weatherDbRepository.getWeatherData(LATITUDE, LONGITUDE, WeatherDataDTO.AIR_CONDITION,
								new DbQueryCallback<WeatherDataDTO>() {
									@Override
									public void onResultSuccessful(WeatherDataDTO resultAirConditionDto) {
										Gson gson = new Gson();
										NearbyMsrstnListRoot nearbyMsrstnListRoot = gson.fromJson(
												resultNearByMsrstnListDto.getJson(), NearbyMsrstnListRoot.class);

										MsrstnAcctoRltmMesureDnstyRoot root = gson.fromJson(resultAirConditionDto.getJson(),
												MsrstnAcctoRltmMesureDnstyRoot.class);

										AirConditionResult airConditionResult = new AirConditionResult();
										airConditionResult.setAirConditionFinalData(root.getResponse().getBody(),
												nearbyMsrstnListRoot,
												new Date(Long.parseLong(resultAirConditionDto.getDownloadedDate())));

										weatherDataCallback.isSuccessful(airConditionResult);
									}

									@Override
									public void onResultNoData() {
										refresh(weatherDataCallback);
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
	public void refresh(WeatherDataCallback<AirConditionResult> weatherDataCallback) {
		Utmk utmk = Utmk.valueOf(new LatLng(Double.parseDouble(LATITUDE), Double.parseDouble(LONGITUDE)));

		NearbyMsrstnListParameter parameter = new NearbyMsrstnListParameter();
		parameter.setTmX(String.valueOf(utmk.x));
		parameter.setTmY(String.valueOf(utmk.y));

		findAirConditionStationDownloader.getNearbyMsrstnList(parameter, new JsonDownloader<JsonObject>() {
			@Override
			public void onResponseSuccessful(JsonObject nearbyMsrstnJsonObject) {
				Gson gson = new Gson();
				nearbyMsrstnListRoot = gson.fromJson(nearbyMsrstnJsonObject.toString(),
						NearbyMsrstnListRoot.class);

				new WeatherDataHeaderChecker() {
					@Override
					public void isSuccessful() {
						WeatherDataDTO nearbyMsrstnListDTO = new WeatherDataDTO();
						nearbyMsrstnListDTO.setLatitude(LATITUDE);
						nearbyMsrstnListDTO.setLongitude(LONGITUDE);
						nearbyMsrstnListDTO.setDataType(WeatherDataDTO.NEAR_BY_MSRSTN_LIST);
						nearbyMsrstnListDTO.setJson(nearbyMsrstnJsonObject.toString());

						final Calendar calendar = Calendar.getInstance(ClockUtil.TIME_ZONE);
						nearbyMsrstnListDTO.setDownloadedDate(String.valueOf(calendar.getTimeInMillis()));
						nearbyMsrstnListDTO.setBaseDateTime(String.valueOf(calendar.getTimeInMillis()));

						weatherDbRepository.contains(LATITUDE, LONGITUDE, WeatherDataDTO.NEAR_BY_MSRSTN_LIST,
								new DbQueryCallback<Boolean>() {
									@Override
									public void onResultSuccessful(Boolean isContain) {
										if (isContain) {
											weatherDbRepository.update(LATITUDE, LONGITUDE, WeatherDataDTO.NEAR_BY_MSRSTN_LIST,
													nearbyMsrstnJsonObject.toString(), nearbyMsrstnListDTO.getDownloadedDate(),
													nearbyMsrstnListDTO.getBaseDateTime(), null);
										} else {
											weatherDbRepository.insert(nearbyMsrstnListDTO, null);
										}
									}

									@Override
									public void onResultNoData() {

									}
								});
						MsrstnAcctoRltmMesureDnstyParameter msrstnAcctoRltmMesureDnstyParameter = new MsrstnAcctoRltmMesureDnstyParameter();
						msrstnAcctoRltmMesureDnstyParameter.setDataTerm(MsrstnAcctoRltmMesureDnstyParameter.DATATERM_DAILY);
						msrstnAcctoRltmMesureDnstyParameter.setStationName(
								nearbyMsrstnListRoot.getResponse().getBody().getItems().get(0).getStationName());

						airConditionDownloader.getMsrstnAcctoRltmMesureDnsty(msrstnAcctoRltmMesureDnstyParameter,
								new JsonDownloader<JsonObject>() {
									@Override
									public void onResponseSuccessful(JsonObject msrstnAcctoRltmMesureDnstyResult) {
										MsrstnAcctoRltmMesureDnstyRoot root = gson.fromJson(msrstnAcctoRltmMesureDnstyResult.toString(),
												MsrstnAcctoRltmMesureDnstyRoot.class);
										new WeatherDataHeaderChecker() {
											@Override
											public void isSuccessful() {
												WeatherDataDTO msrstnAcctoRltmMesureDnstyWeatherDataDTO = new WeatherDataDTO();
												msrstnAcctoRltmMesureDnstyWeatherDataDTO.setLatitude(LATITUDE);
												msrstnAcctoRltmMesureDnstyWeatherDataDTO.setLongitude(LONGITUDE);
												msrstnAcctoRltmMesureDnstyWeatherDataDTO.setDataType(WeatherDataDTO.AIR_CONDITION);
												msrstnAcctoRltmMesureDnstyWeatherDataDTO.setJson(msrstnAcctoRltmMesureDnstyResult.toString());
												msrstnAcctoRltmMesureDnstyWeatherDataDTO.setDownloadedDate(String.valueOf(calendar.getTimeInMillis()));
												msrstnAcctoRltmMesureDnstyWeatherDataDTO.setBaseDateTime(String.valueOf(calendar.getTimeInMillis()));

												//db 삽입, 갱신
												weatherDbRepository.contains(msrstnAcctoRltmMesureDnstyWeatherDataDTO.getLatitude(),
														msrstnAcctoRltmMesureDnstyWeatherDataDTO.getLongitude(), WeatherDataDTO.AIR_CONDITION,
														new DbQueryCallback<Boolean>() {
															@Override
															public void onResultSuccessful(Boolean isContains) {
																if (isContains) {
																	weatherDbRepository.update(
																			msrstnAcctoRltmMesureDnstyWeatherDataDTO.getLatitude(),
																			msrstnAcctoRltmMesureDnstyWeatherDataDTO.getLongitude(),
																			WeatherDataDTO.AIR_CONDITION,
																			msrstnAcctoRltmMesureDnstyWeatherDataDTO.getJson(),
																			msrstnAcctoRltmMesureDnstyWeatherDataDTO.getDownloadedDate(),
																			msrstnAcctoRltmMesureDnstyWeatherDataDTO.getBaseDateTime(),
																			null);
																} else {
																	weatherDbRepository.insert(msrstnAcctoRltmMesureDnstyWeatherDataDTO,
																			null);

																}
															}

															@Override
															public void onResultNoData() {

															}
														});

												AirConditionResult airConditionResult = new AirConditionResult();
												airConditionResult.setAirConditionFinalData(
														root.getResponse().getBody(),
														nearbyMsrstnListRoot, new Date(Long.parseLong(
																msrstnAcctoRltmMesureDnstyWeatherDataDTO.getDownloadedDate())));

												weatherDataCallback.isSuccessful(airConditionResult);
											}

											@Override
											public void isFailure(Exception e) {
												weatherDataCallback.isFailure(e);
											}
										}.processResult(root.getResponse().getHeader());
									}

									@Override
									public void onResponseFailed(Exception e) {
										weatherDataCallback.isFailure(e);
									}
								});
					}

					@Override
					public void isFailure(Exception e) {
						weatherDataCallback.isFailure(e);
					}
				}.processResult(nearbyMsrstnListRoot.getResponse().getHeader());
			}

			@Override
			public void onResponseFailed(Exception e) {
				weatherDataCallback.isFailure(e);

			}
		});

	}

	public NearbyMsrstnListRoot getNearbyMsrstnListRoot() {
		return nearbyMsrstnListRoot;
	}

}