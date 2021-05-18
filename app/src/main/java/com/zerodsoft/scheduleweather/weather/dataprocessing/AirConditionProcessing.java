package com.zerodsoft.scheduleweather.weather.dataprocessing;

import android.content.Context;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MsrstnAcctoRltmMesureDnstyParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.NearbyMsrstnListParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.TransCoordParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResult;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar.AirConditionResult;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.repository.AirConditionDownloader;
import com.zerodsoft.scheduleweather.weather.repository.FindAirConditionStationDownloader;
import com.zerodsoft.scheduleweather.weather.repository.SgisTranscoord;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDbRepository;
import com.zerodsoft.scheduleweather.weather.repository.WeatherRepository;

import java.util.Date;

public class AirConditionProcessing extends WeatherDataProcessing<AirConditionResult> {
	public AirConditionProcessing(Context context, String LATITUDE, String LONGITUDE) {
		super(context, LATITUDE, LONGITUDE);
	}

	private final AirConditionDownloader airConditionDownloader = new AirConditionDownloader();

	private final FindAirConditionStationDownloader findAirConditionStationDownloader = new FindAirConditionStationDownloader();

	private final SgisTranscoord sgisTranscoord = new SgisTranscoord();

	@Override
	public void getWeatherData(WeatherDataCallback<AirConditionResult> weatherDataCallback) {
		weatherDbRepository.getWeatherData(LATITUDE, LONGITUDE, WeatherDataDTO.AIR_CONDITION,
				new CarrierMessagingService.ResultCallback<WeatherDataDTO>() {
					@Override
					public void onReceiveResult(@NonNull WeatherDataDTO savedWeatherDataDTO) throws RemoteException {
						if (savedWeatherDataDTO == null) {
							refresh(weatherDataCallback);
						} else {
							Gson gson = new Gson();
							MsrstnAcctoRltmMesureDnstyRoot root = gson.fromJson(savedWeatherDataDTO.getJson(),
									MsrstnAcctoRltmMesureDnstyRoot.class);

							AirConditionResult airConditionResult = new AirConditionResult();
							airConditionResult.setAirConditionFinalData(root.getResponse().getBody().getItem().get(0),
									new Date(Long.parseLong(savedWeatherDataDTO.getDownloadedDate())));

							weatherDataCallback.isSuccessful(airConditionResult);
						}
					}
				});
	}

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

				findAirConditionStationDownloader.getNearbyMsrstnList(parameter, new JsonDownloader<NearbyMsrstnListRoot>() {
					@Override
					public void onResponseSuccessful(NearbyMsrstnListRoot result) {
						MsrstnAcctoRltmMesureDnstyParameter msrstnAcctoRltmMesureDnstyParameter = new MsrstnAcctoRltmMesureDnstyParameter();
						msrstnAcctoRltmMesureDnstyParameter.setDataTerm(MsrstnAcctoRltmMesureDnstyParameter.DATATERM_DAILY);
						msrstnAcctoRltmMesureDnstyParameter.setStationName(result.getResponse().getBody().getItems().get(0).getStationName());

						airConditionDownloader.getMsrstnAcctoRltmMesureDnsty(msrstnAcctoRltmMesureDnstyParameter, new JsonDownloader<JsonObject>() {
							@Override
							public void onResponseSuccessful(JsonObject result) {
								Gson gson = new Gson();
								MsrstnAcctoRltmMesureDnstyRoot root = gson.fromJson(result.toString(), MsrstnAcctoRltmMesureDnstyRoot.class);

								WeatherDataDTO weatherDataDTO = new WeatherDataDTO();
								weatherDataDTO.setLatitude(LATITUDE);
								weatherDataDTO.setLongitude(LONGITUDE);
								weatherDataDTO.setDataType(WeatherDataDTO.AIR_CONDITION);
								weatherDataDTO.setJson(result.toString());
								weatherDataDTO.setDownloadedDate(String.valueOf(System.currentTimeMillis()));

								//db 삽입, 갱신
								weatherDbRepository.contains(weatherDataDTO.getLatitude(), weatherDataDTO.getLongitude(), WeatherDataDTO.AIR_CONDITION,
										new CarrierMessagingService.ResultCallback<Boolean>() {
											@Override
											public void onReceiveResult(@NonNull Boolean isContains) throws RemoteException {
												if (isContains) {
													weatherDbRepository.update(weatherDataDTO.getLatitude(), weatherDataDTO.getLongitude(),
															WeatherDataDTO.AIR_CONDITION, weatherDataDTO.getJson(), weatherDataDTO.getDownloadedDate(),
															new CarrierMessagingService.ResultCallback<Boolean>() {
																@Override
																public void onReceiveResult(@NonNull Boolean isUpdated) throws RemoteException {
																	AirConditionResult airConditionResult = new AirConditionResult();
																	airConditionResult.setAirConditionFinalData(root.getResponse().getBody().getItem().get(0),
																			new Date(Long.parseLong(weatherDataDTO.getDownloadedDate())));

																	weatherDataCallback.isSuccessful(airConditionResult);
																}
															});
												} else {
													weatherDbRepository.insert(weatherDataDTO, new CarrierMessagingService.ResultCallback<WeatherDataDTO>() {
														@Override
														public void onReceiveResult(@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException {
															AirConditionResult airConditionResult = new AirConditionResult();
															airConditionResult.setAirConditionFinalData(root.getResponse().getBody().getItem().get(0),
																	new Date(Long.parseLong(weatherDataDTO.getDownloadedDate())));

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

}
