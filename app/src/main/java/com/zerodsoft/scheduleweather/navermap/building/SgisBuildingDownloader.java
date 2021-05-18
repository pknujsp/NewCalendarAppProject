package com.zerodsoft.scheduleweather.navermap.building;

import android.service.carrier.CarrierMessagingService;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.building.BuildingAreaParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.building.BuildingAttributeParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.building.FloorCompanyInfoParameter;
import com.zerodsoft.scheduleweather.retrofit.paremeters.sgis.building.FloorEtcFacilityParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth.SgisAuthResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.SgisBuildingRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingarea.BuildingAreaResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingattribute.BuildingAttributeResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.floorcompanyinfo.FloorCompanyInfoResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.flooretcfacility.FloorEtcFacilityResponse;
import com.zerodsoft.scheduleweather.sgis.SgisAuth;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SgisBuildingDownloader {
	public void getBuildingList(BuildingAreaParameter parameter, JsonDownloader<BuildingAreaResponse> callback) {
		if (SgisAuth.hasAccessToken()) {
			parameter.setAccessToken(SgisAuth.getAccessToken());
		} else {
			SgisAuth.auth(new JsonDownloader<SgisAuthResponse>() {
				@Override
				public void onResponseSuccessful(SgisAuthResponse result) {
					SgisAuth.setSgisAuthResponse(result);
					parameter.setAccessToken(result.getResult().getAccessToken());
					getBuildingList(parameter, callback);
				}

				@Override
				public void onResponseFailed(Exception e) {
					callback.onResponseFailed(e);
				}
			});
			return;
		}

		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_FIGURE);
		Call<BuildingAreaResponse> call = querys.buildingArea(parameter.toMap());

		call.enqueue(new Callback<BuildingAreaResponse>() {
			@SneakyThrows
			@Override
			public void onResponse(Call<BuildingAreaResponse> call, Response<BuildingAreaResponse> response) {
				callback.processResult(response);
			}

			@SneakyThrows
			@Override
			public void onFailure(Call<BuildingAreaResponse> call, Throwable t) {
				callback.processResult(t);
			}
		});
	}

	public void getBuildingAttribute(BuildingAttributeParameter parameter,
	                                 JsonDownloader<BuildingAttributeResponse> callback) {
		if (SgisAuth.hasAccessToken()) {
			parameter.setAccessToken(SgisAuth.getAccessToken());
		} else {
			SgisAuth.auth(new JsonDownloader<SgisAuthResponse>() {
				@Override
				public void onResponseSuccessful(SgisAuthResponse result) {
					SgisAuth.setSgisAuthResponse(result);
					parameter.setAccessToken(result.getResult().getAccessToken());
					getBuildingAttribute(parameter, callback);
				}

				@Override
				public void onResponseFailed(Exception e) {
					callback.onResponseFailed(e);
				}
			});
			return;
		}
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_FIGURE);
		Call<BuildingAttributeResponse> call = querys.buildingAttribute(parameter.toMap());

		call.enqueue(new Callback<BuildingAttributeResponse>() {
			@SneakyThrows
			@Override
			public void onResponse(Call<BuildingAttributeResponse> call, Response<BuildingAttributeResponse> response) {
				callback.processResult(response);
			}

			@SneakyThrows
			@Override
			public void onFailure(Call<BuildingAttributeResponse> call, Throwable t) {
				callback.processResult(t);
			}
		});
	}

	public void getFloorEtcFacility(FloorEtcFacilityParameter parameter, JsonDownloader<FloorEtcFacilityResponse> callback) {
		if (SgisAuth.hasAccessToken()) {
			parameter.setAccessToken(SgisAuth.getAccessToken());
		} else {
			SgisAuth.auth(new JsonDownloader<SgisAuthResponse>() {
				@Override
				public void onResponseSuccessful(SgisAuthResponse result) {
					SgisAuth.setSgisAuthResponse(result);
					parameter.setAccessToken(result.getResult().getAccessToken());
					getFloorEtcFacility(parameter, callback);
				}

				@Override
				public void onResponseFailed(Exception e) {
					callback.onResponseFailed(e);
				}
			});
			return;
		}

		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_FIGURE);
		Call<FloorEtcFacilityResponse> call = querys.floorEtcFacility(parameter.toMap());

		call.enqueue(new Callback<FloorEtcFacilityResponse>() {
			@SneakyThrows
			@Override
			public void onResponse(Call<FloorEtcFacilityResponse> call, Response<FloorEtcFacilityResponse> response) {
				callback.processResult(response);
			}

			@SneakyThrows
			@Override
			public void onFailure(Call<FloorEtcFacilityResponse> call, Throwable t) {
				callback.processResult(t);
			}
		});
	}

	public void getFloorCompanyInfo(FloorCompanyInfoParameter parameter, JsonDownloader<FloorCompanyInfoResponse> callback) {
		if (SgisAuth.hasAccessToken()) {
			parameter.setAccessToken(SgisAuth.getAccessToken());
		} else {
			SgisAuth.auth(new JsonDownloader<SgisAuthResponse>() {
				@Override
				public void onResponseSuccessful(SgisAuthResponse result) {
					SgisAuth.setSgisAuthResponse(result);
					parameter.setAccessToken(result.getResult().getAccessToken());
					getFloorCompanyInfo(parameter, callback);
				}

				@Override
				public void onResponseFailed(Exception e) {
					callback.onResponseFailed(e);
				}
			});
			return;
		}
		Querys querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.SGIS_FIGURE);
		Call<FloorCompanyInfoResponse> call = querys.floorCompanyInfo(parameter.toMap());

		call.enqueue(new Callback<FloorCompanyInfoResponse>() {
			@SneakyThrows
			@Override
			public void onResponse(Call<FloorCompanyInfoResponse> call, Response<FloorCompanyInfoResponse> response) {
				callback.processResult(response);
			}

			@SneakyThrows
			@Override
			public void onFailure(Call<FloorCompanyInfoResponse> call, Throwable t) {
				callback.processResult(t);
			}
		});
	}
}
