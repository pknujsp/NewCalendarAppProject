package com.zerodsoft.scheduleweather.weather.dataprocessing;

import android.content.Context;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zerodsoft.scheduleweather.retrofit.paremeters.MsrstnAcctoRltmMesureDnstyParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.NearbyMsrstnListParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.TransCoordParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty.MsrstnAcctoRltmMesureDnstyRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListBody;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList.NearbyMsrstnListRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth.SgisAuthResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.transcoord.TransCoordResult;
import com.zerodsoft.scheduleweather.room.dto.WeatherDataDTO;
import com.zerodsoft.scheduleweather.sgis.SgisAuth;
import com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar.AirConditionFinalData;
import com.zerodsoft.scheduleweather.weather.aircondition.airconditionbar.AirConditionResult;
import com.zerodsoft.scheduleweather.weather.common.WeatherDataCallback;
import com.zerodsoft.scheduleweather.weather.repository.AirConditionDownloader;
import com.zerodsoft.scheduleweather.weather.repository.FindAirConditionStationDownloader;
import com.zerodsoft.scheduleweather.weather.repository.SgisTranscoord;
import com.zerodsoft.scheduleweather.weather.repository.WeatherDbRepository;
import com.zerodsoft.scheduleweather.weather.repository.WeatherRepository;

import java.util.Date;

public class AirConditionProcessing extends WeatherDataProcessing<AirConditionResult> {
	private WeatherRepository weatherRepository;
	private WeatherDbRepository weatherDbRepository;
	private Context context;
	
	private String latitude;
	private String longitude;
	
	public AirConditionProcessing(Context context) {
		this.context = context;
		this.weatherRepository = new WeatherRepository(context);
		this.weatherDbRepository = new WeatherDbRepository(context);
	}
	
	private final AirConditionDownloader airConditionDownloader = new AirConditionDownloader() {
		@Override
		public void onResponseSuccessful(JsonObject result) {
			Gson gson = new Gson();
			MsrstnAcctoRltmMesureDnstyRoot root = gson.fromJson(result.toString(), MsrstnAcctoRltmMesureDnstyRoot.class);
			msrstnAcctoRltmMesureDnstyBody = root.getResponse().getBody();
			setData(msrstnAcctoRltmMesureDnstyBody);
			
			WeatherDataDTO weatherDataDTO = new WeatherDataDTO();
			weatherDataDTO.setLatitude(String.valueOf(LATITUDE));
			weatherDataDTO.setLongitude(String.valueOf(LONGITUDE));
			weatherDataDTO.setDataType(WeatherDataDTO.AIR_CONDITION);
			weatherDataDTO.setJson(result.toString());
			weatherDataDTO.setDownloadedDate(String.valueOf(System.currentTimeMillis()));
			
			weatherDbViewModel.contains(weatherDataDTO.getLatitude(), weatherDataDTO.getLongitude(), WeatherDataDTO.AIR_CONDITION,
					new CarrierMessagingService.ResultCallback<Boolean>() {
						@Override
						public void onReceiveResult(@NonNull Boolean isContains) throws RemoteException {
							if (isContains) {
								weatherDbViewModel.update(weatherDataDTO.getLatitude(), weatherDataDTO.getLongitude(),
										WeatherDataDTO.AIR_CONDITION, weatherDataDTO.getJson(), weatherDataDTO.getDownloadedDate(),
										new CarrierMessagingService.ResultCallback<Boolean>() {
											@Override
											public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException {
											
											}
										});
							} else {
								weatherDbViewModel.insert(weatherDataDTO, new CarrierMessagingService.ResultCallback<WeatherDataDTO>() {
									@Override
									public void onReceiveResult(@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException {
									
									}
								});
								
							}
						}
					});
			
		}
		
		@Override
		public void onResponseFailed(Exception e) {
		
		}
	};
	
	private final FindAirConditionStationDownloader findAirConditionStationDownloader = new FindAirConditionStationDownloader() {
		@Override
		public void onResponseSuccessful(JsonObject result) {
			Gson gson = new Gson();
			NearbyMsrstnListRoot root = gson.fromJson(result.toString(), NearbyMsrstnListRoot.class);
			NearbyMsrstnListBody nearbyMsrstnListBody = root.getResponse().getBody();
			
			WeatherDataDTO weatherDataDTO = new WeatherDataDTO();
			weatherDataDTO.setLatitude(latitude);
			weatherDataDTO.setLongitude(longitude);
			weatherDataDTO.setDataType(WeatherDataDTO.NEAR_BY_MSRSTN_LIST);
			weatherDataDTO.setJson(result.toString());
			weatherDataDTO.setDownloadedDate(String.valueOf(System.currentTimeMillis()));
			
			weatherDbRepository.contains(latitude, longitude, WeatherDataDTO.NEAR_BY_MSRSTN_LIST,
					new CarrierMessagingService.ResultCallback<Boolean>() {
						@Override
						public void onReceiveResult(@NonNull Boolean isContains) throws RemoteException {
							if (isContains) {
								weatherDbRepository.update(latitude, longitude, WeatherDataDTO.NEAR_BY_MSRSTN_LIST, result.toString(),
										weatherDataDTO.getDownloadedDate(), new CarrierMessagingService.ResultCallback<Boolean>() {
											@Override
											public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException {
											
											}
										});
							} else {
								weatherDbRepository.insert(weatherDataDTO, new CarrierMessagingService.ResultCallback<WeatherDataDTO>() {
									@Override
									public void onReceiveResult(@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException {
									
									}
								});
							}
						}
					});
			
			msrstnAcctoRltmMesureDnstyParameter = new MsrstnAcctoRltmMesureDnstyParameter();
			msrstnAcctoRltmMesureDnstyParameter.setDataTerm(MsrstnAcctoRltmMesureDnstyParameter.DATATERM_DAILY);
			msrstnAcctoRltmMesureDnstyParameter.setStationName(nearbyMsrstnListBody.getItems().get(0).getStationName());
			
			airConditionDownloader.getMsrstnAcctoRltmMesureDnsty(msrstnAcctoRltmMesureDnstyParameter);
		}
		
		@Override
		public void onResponseFailed(Exception e) {
		}
	};
	
	
	private final SgisTranscoord sgisTranscoord = new SgisTranscoord() {
		@Override
		public void onResponseSuccessful(TransCoordResponse result) {
			TransCoordResult transCoordResult = result.getResult();
			if (transCoordResult.getPosX() == null) {
				onResponseFailed(new Exception());
			}
			NearbyMsrstnListParameter parameter = new NearbyMsrstnListParameter();
			parameter.setTmX(transCoordResult.getPosX());
			parameter.setTmY(transCoordResult.getPosY());
			
			findAirConditionStationDownloader.getNearbyMsrstnList(parameter);
		}
		
		@Override
		public void onResponseFailed(Exception e) {
		
		}
	};
	
	@Override
	public void getWeatherData(String latitude, String longitude, WeatherDataCallback<AirConditionResult> weatherDataCallback) {
		this.latitude = latitude;
		this.longitude = longitude;
		
		weatherDbRepository.getWeatherData(latitude, longitude WeatherDataDTO.NEAR_BY_MSRSTN_LIST,
				new CarrierMessagingService.ResultCallback<WeatherDataDTO>() {
					@Override
					public void onReceiveResult(@NonNull WeatherDataDTO nearByMsrstn) throws RemoteException {
						NearbyMsrstnListBody nearbyMsrstnListBody = null;
						
						if (nearByMsrstn != null) {
							Gson gson = new Gson();
							NearbyMsrstnListRoot root = gson.fromJson(nearByMsrstn.getJson(), NearbyMsrstnListRoot.class);
							nearbyMsrstnListBody = root.getResponse().getBody();
						}
						
						weatherDbRepository.getWeatherData(latitude, longitude, WeatherDataDTO.AIR_CONDITION,
								new CarrierMessagingService.ResultCallback<WeatherDataDTO>() {
									@Override
									public void onReceiveResult(@NonNull WeatherDataDTO weatherDataDTO) throws RemoteException {
										if (weatherDataDTO == null || nearByMsrstn == null) {
											refresh();
										} else {
											Gson gson = new Gson();
											MsrstnAcctoRltmMesureDnstyRoot root = gson.fromJson(weatherDataDTO.getJson(),
													MsrstnAcctoRltmMesureDnstyRoot.class);
											
											AirConditionResult airConditionResult = new AirConditionResult();
											airConditionResult.setAirConditionFinalData(root.getResponse().getBody().getItem().get(0),
													new Date(Long.parseLong(weatherDataDTO.getDownloadedDate())));
											
											weatherDataCallback.isSuccessful(airConditionResult);
										}
									}
								});
						
					}
				});
	}
	
	@Override
	public void refresh() {
		TransCoordParameter parameter = new TransCoordParameter();
		parameter.setSrc(TransCoordParameter.WGS84);
		parameter.setDst(TransCoordParameter.JUNGBU_ORIGIN);
		parameter.setPosX(longitude);
		parameter.setPosY(latitude);
		
		sgisTranscoord.transcoord(parameter);
	}
	
}
