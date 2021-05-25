package com.zerodsoft.scheduleweather.weather.dataprocessing;

import android.content.Context;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Tm128;
import com.naver.maps.geometry.Utmk;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MsrstnAcctoRltmMesureDnstyParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.NearbyMsrstnListParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.TransCoordParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResult;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar.AirConditionResult;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataHeaderChecker;
import com.zerodsoft.scheduleweather.weather.repository.AirConditionDownloader;
import com.zerodsoft.scheduleweather.weather.repository.FindAirConditionStationDownloader;
import com.zerodsoft.scheduleweather.weather.repository.SgisTranscoord;

import java.util.Date;

public class AirConditionProcessing extends WeatherDataProcessing<AirConditionResult> {
	public AirConditionProcessing(Context context, String LATITUDE, String LONGITUDE) {
		super(context, LATITUDE, LONGITUDE);
	}

	private final AirConditionDownloader airConditionDownloader = AirConditionDownloader.getInstance();

	private final FindAirConditionStationDownloader findAirConditionStationDownloader = new FindAirConditionStationDownloader();

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
										airConditionResult.setAirConditionFinalData(root.getResponse().getBody().getItem().get(0),
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
				NearbyMsrstnListRoot nearbyMsrstnListRoot = gson.fromJson(nearbyMsrstnJsonObject.toString(),
						NearbyMsrstnListRoot.class);

				new WeatherDataHeaderChecker() {
					@Override
					public void isSuccessful() {
						WeatherDataDTO nearbyMsrstnListDTO = new WeatherDataDTO();
						nearbyMsrstnListDTO.setLatitude(LATITUDE);
						nearbyMsrstnListDTO.setLongitude(LONGITUDE);
						nearbyMsrstnListDTO.setDataType(WeatherDataDTO.NEAR_BY_MSRSTN_LIST);
						nearbyMsrstnListDTO.setJson(nearbyMsrstnJsonObject.toString());
						nearbyMsrstnListDTO.setDownloadedDate(String.valueOf(System.currentTimeMillis()));

						weatherDbRepository.contains(LATITUDE, LONGITUDE, WeatherDataDTO.NEAR_BY_MSRSTN_LIST,
								new DbQueryCallback<Boolean>() {
									@Override
									public void onResultSuccessful(Boolean isContain) {
										if (isContain) {
											weatherDbRepository.update(LATITUDE, LONGITUDE, WeatherDataDTO.NEAR_BY_MSRSTN_LIST,
													nearbyMsrstnJsonObject.toString(), nearbyMsrstnListDTO.getDownloadedDate(),
													new DbQueryCallback<Boolean>() {
														@Override
														public void onResultSuccessful(Boolean resultDto) {

														}

														@Override
														public void onResultNoData() {

														}
													});
										} else {
											weatherDbRepository.insert(nearbyMsrstnListDTO,
													new DbQueryCallback<WeatherDataDTO>() {
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
												msrstnAcctoRltmMesureDnstyWeatherDataDTO.setDownloadedDate(
														String.valueOf(System.currentTimeMillis()));

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
																			new DbQueryCallback<Boolean>() {
																				@Override
																				public void onResultSuccessful(Boolean resultDto) {

																				}

																				@Override
																				public void onResultNoData() {

																				}
																			});
																} else {
																	weatherDbRepository.insert(msrstnAcctoRltmMesureDnstyWeatherDataDTO,
																			new DbQueryCallback<WeatherDataDTO>() {
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

												AirConditionResult airConditionResult = new AirConditionResult();
												airConditionResult.setAirConditionFinalData(
														root.getResponse().getBody().getItem().get(0),
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

}

/*
@Override
	public void refresh(WeatherDataCallback<AirConditionResult> weatherDataCallback) {
		TransCoordParameter parameter = new TransCoordParameter();
		parameter.setSrc(TransCoordParameter.WGS84);
		parameter.setDst(TransCoordParameter.JUNGBU_ORIGIN);
		parameter.setPosX(LONGITUDE);
		parameter.setPosY(LATITUDE);

		sgisTranscoord.transcoord(parameter, new JsonDownloader<TransCoordResponse>() {
			@Override
			public void onResponseSuccessful(TransCoordResponse result) {
				TransCoordResult transCoordResult = result.getResult();
				if (transCoordResult.getPosX() == null) {
					onResponseFailed(new Exception());
				}

				NearbyMsrstnListParameter parameter = new NearbyMsrstnListParameter();
				parameter.setTmX(transCoordResult.getPosX());
				parameter.setTmY(transCoordResult.getPosY());

				findAirConditionStationDownloader.getNearbyMsrstnList(parameter, new JsonDownloader<JsonObject>() {
					@Override
					public void onResponseSuccessful(JsonObject nearbyMsrstnJsonObject) {
						Gson gson = new Gson();
						NearbyMsrstnListRoot nearbyMsrstnListRoot = gson.fromJson(nearbyMsrstnJsonObject.toString(),
								NearbyMsrstnListRoot.class);

						WeatherDataDTO nearbyMsrstnListDTO = new WeatherDataDTO();
						nearbyMsrstnListDTO.setLatitude(LATITUDE);
						nearbyMsrstnListDTO.setLongitude(LONGITUDE);
						nearbyMsrstnListDTO.setDataType(WeatherDataDTO.NEAR_BY_MSRSTN_LIST);
						nearbyMsrstnListDTO.setJson(nearbyMsrstnJsonObject.toString());
						nearbyMsrstnListDTO.setDownloadedDate(String.valueOf(System.currentTimeMillis()));

						weatherDbRepository.contains(LATITUDE, LONGITUDE, WeatherDataDTO.NEAR_BY_MSRSTN_LIST,
								new CarrierMessagingService.ResultCallback<Boolean>() {
									@Override
									public void onReceiveResult(@NonNull Boolean isContains) throws RemoteException {
										if (isContains) {
											weatherDbRepository.update(LATITUDE, LONGITUDE, WeatherDataDTO.NEAR_BY_MSRSTN_LIST,
													nearbyMsrstnJsonObject.toString(), nearbyMsrstnListDTO.getDownloadedDate(),
													new CarrierMessagingService.ResultCallback<Boolean>() {
														@Override
														public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException {

														}
													});
										} else {
											weatherDbRepository.insert(nearbyMsrstnListDTO,
													new CarrierMessagingService.ResultCallback<WeatherDataDTO>() {
														@Override
														public void onReceiveResult(
																@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException {

														}
													});
										}
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

										WeatherDataDTO msrstnAcctoRltmMesureDnstyWeatherDataDTO = new WeatherDataDTO();
										msrstnAcctoRltmMesureDnstyWeatherDataDTO.setLatitude(LATITUDE);
										msrstnAcctoRltmMesureDnstyWeatherDataDTO.setLongitude(LONGITUDE);
										msrstnAcctoRltmMesureDnstyWeatherDataDTO.setDataType(WeatherDataDTO.AIR_CONDITION);
										msrstnAcctoRltmMesureDnstyWeatherDataDTO.setJson(msrstnAcctoRltmMesureDnstyResult.toString());
										msrstnAcctoRltmMesureDnstyWeatherDataDTO.setDownloadedDate(
												String.valueOf(System.currentTimeMillis()));

										//db 삽입, 갱신
										weatherDbRepository.contains(msrstnAcctoRltmMesureDnstyWeatherDataDTO.getLatitude(),
												msrstnAcctoRltmMesureDnstyWeatherDataDTO.getLongitude(), WeatherDataDTO.AIR_CONDITION,
												new CarrierMessagingService.ResultCallback<Boolean>() {
													@Override
													public void onReceiveResult(@NonNull Boolean isContains) throws RemoteException {
														if (isContains) {
															weatherDbRepository.update(
																	msrstnAcctoRltmMesureDnstyWeatherDataDTO.getLatitude(),
																	msrstnAcctoRltmMesureDnstyWeatherDataDTO.getLongitude(),
																	WeatherDataDTO.AIR_CONDITION,
																	msrstnAcctoRltmMesureDnstyWeatherDataDTO.getJson(),
																	msrstnAcctoRltmMesureDnstyWeatherDataDTO.getDownloadedDate(),
																	new CarrierMessagingService.ResultCallback<Boolean>() {
																		@Override
																		public void onReceiveResult(
																				@NonNull Boolean isUpdated) throws RemoteException {
																			AirConditionResult airConditionResult = new AirConditionResult();
																			airConditionResult.setAirConditionFinalData(
																					root.getResponse().getBody().getItem().get(0),
																					nearbyMsrstnListRoot, new Date(Long.parseLong(
																							msrstnAcctoRltmMesureDnstyWeatherDataDTO.getDownloadedDate())));

																			weatherDataCallback.isSuccessful(airConditionResult);
																		}
																	});
														} else {
															weatherDbRepository.insert(msrstnAcctoRltmMesureDnstyWeatherDataDTO,
																	new CarrierMessagingService.ResultCallback<WeatherDataDTO>() {
																		@Override
																		public void onReceiveResult(
																				@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException {
																			AirConditionResult airConditionResult = new AirConditionResult();
																			airConditionResult.setAirConditionFinalData(
																					root.getResponse().getBody().getItem().get(0),
																					nearbyMsrstnListRoot, new Date(Long.parseLong(
																							weatherDataDTO.getDownloadedDate())));

																			weatherDataCallback.isSuccessful(airConditionResult);
																		}
																	});

														}
													}
												});
									}

									@Override
									public void onResponseFailed(Exception e) {
										weatherDataCallback.isFailure(e);

									}
								});
					}

					@Override
					public void onResponseFailed(Exception e) {
						weatherDataCallback.isFailure(e);

					}
				});
			}

			@Override
			public void onResponseFailed(Exception e) {
				weatherDataCallback.isFailure(e);
			}
		});
	}

 */
