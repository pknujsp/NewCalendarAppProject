package com.zerodsoft.scheduleweather.event.common.repository;

import android.content.Context;
import android.service.carrier.CarrierMessagingService;

import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocationDao;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.HttpCommunicationClient;
import com.zerodsoft.scheduleweather.retrofit.Querys;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.AppDb;
import com.zerodsoft.scheduleweather.room.dao.LocationDAO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

import java.util.Map;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationRepository implements ILocationDao {
	private MutableLiveData<LocationDTO> locationLiveData;
	private LocationDAO locationDAO;
	private Querys querys;

	public LocationRepository(Context context) {
		locationDAO = AppDb.getInstance(context).locationDAO();
		locationLiveData = new MutableLiveData<>();
	}

	@Override
	public void getAddressItem(LocalApiPlaceParameter parameter,
	                           CarrierMessagingService.ResultCallback<DataWrapper<AddressResponseDocuments>> callback) {
		querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
		Map<String, String> queryMap = parameter.getParameterMap();
		Call<AddressKakaoLocalResponse> call = querys.getAddress(queryMap);

		call.enqueue(new Callback<AddressKakaoLocalResponse>() {
			@SneakyThrows
			@Override
			public void onResponse(Call<AddressKakaoLocalResponse> call, Response<AddressKakaoLocalResponse> response) {
				DataWrapper<AddressResponseDocuments> dataWrapper = null;

				if (response.isSuccessful()) {
					AddressResponseDocuments document = response.body().getAddressResponseDocumentsList().get(0);
					dataWrapper = new DataWrapper<>(document);
				} else {
					dataWrapper = new DataWrapper<>(new NullPointerException());
				}

				callback.onReceiveResult(dataWrapper);
			}

			@SneakyThrows
			@Override
			public void onFailure(Call<AddressKakaoLocalResponse> call, Throwable t) {
				DataWrapper<AddressResponseDocuments> dataWrapper = new DataWrapper<>(new Exception(t));
				callback.onReceiveResult(dataWrapper);
			}
		});
	}

	@Override
	public void getPlaceItem(LocalApiPlaceParameter parameter, String placeId, JsonDownloader<PlaceKakaoLocalResponse> callback) {
		querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
		Map<String, String> queryMap = parameter.getParameterMap();
		Call<PlaceKakaoLocalResponse> call = null;

		if (parameter.getQuery() == null) {
			call = querys.getPlaceCategory(queryMap);
		} else {
			call = querys.getPlaceKeyword(queryMap);
		}

		call.enqueue(new Callback<PlaceKakaoLocalResponse>() {
			@SneakyThrows
			@Override
			public void onResponse(Call<PlaceKakaoLocalResponse> call, Response<PlaceKakaoLocalResponse> response) {
				callback.processResult(response);
			}

			@SneakyThrows
			@Override
			public void onFailure(Call<PlaceKakaoLocalResponse> call, Throwable t) {
				callback.processResult(t);
			}
		});
	}


	@Override
	public void getLocation(int calendarId, long eventId, DbQueryCallback<LocationDTO> resultCallback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				LocationDTO locationDTO = locationDAO.select(calendarId, eventId);
				locationLiveData.postValue(locationDTO == null ? new LocationDTO() : locationDTO);
				resultCallback.processResult(locationDTO);
			}
		});
	}

	@Override
	public void hasDetailLocation(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> resultCallback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				int result = locationDAO.hasLocation(calendarId, eventId);
				resultCallback.onReceiveResult(result == 1);
			}
		});
	}

	@Override
	public void addLocation(LocationDTO location, CarrierMessagingService.ResultCallback<Boolean> resultCallback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				long result = locationDAO.insert(location);
				resultCallback.onReceiveResult(result > -1);
			}
		});
	}

	@Override
	public void removeLocation(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> resultCallback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				locationDAO.delete(calendarId, eventId);
				resultCallback.onReceiveResult(true);
			}
		});
	}

	@Override
	public void modifyLocation(LocationDTO location, CarrierMessagingService.ResultCallback<Boolean> resultCallback) {
		App.executorService.execute(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				locationDAO.update(location);
				resultCallback.onReceiveResult(true);
			}
		});
	}

	public MutableLiveData<LocationDTO> getLocationLiveData() {
		return locationLiveData;
	}
}
