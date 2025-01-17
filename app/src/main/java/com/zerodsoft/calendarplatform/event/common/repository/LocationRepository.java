package com.zerodsoft.calendarplatform.event.common.repository;

import android.content.Context;

import com.zerodsoft.calendarplatform.activity.App;
import com.zerodsoft.calendarplatform.common.classes.JsonDownloader;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.event.common.interfaces.ILocationDao;
import com.zerodsoft.calendarplatform.retrofit.HttpCommunicationClient;
import com.zerodsoft.calendarplatform.retrofit.Querys;
import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.calendarplatform.room.AppDb;
import com.zerodsoft.calendarplatform.room.dao.LocationDAO;
import com.zerodsoft.calendarplatform.room.dto.LocationDTO;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationRepository implements ILocationDao {
	private LocationDAO locationDAO;
	private Querys querys;

	public LocationRepository(Context context) {
		locationDAO = AppDb.getInstance(context).locationDAO();
	}

	@Override
	public void getAddressItem(LocalApiPlaceParameter parameter,
	                           JsonDownloader<AddressKakaoLocalResponse> callback) {
		querys = HttpCommunicationClient.getApiService(HttpCommunicationClient.KAKAO);
		Map<String, String> queryMap = parameter.getParameterMap();
		Call<AddressKakaoLocalResponse> call = querys.getAddress(queryMap);

		call.enqueue(new Callback<AddressKakaoLocalResponse>() {
			@Override
			public void onResponse(Call<AddressKakaoLocalResponse> call, Response<AddressKakaoLocalResponse> response) {
				callback.processResult(response);
			}

			@Override
			public void onFailure(Call<AddressKakaoLocalResponse> call, Throwable t) {
				callback.processResult(t);
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
			@Override
			public void onResponse(Call<PlaceKakaoLocalResponse> call, Response<PlaceKakaoLocalResponse> response) {
				callback.processResult(response);
			}

			@Override
			public void onFailure(Call<PlaceKakaoLocalResponse> call, Throwable t) {
				callback.processResult(t);
			}
		});
	}


	@Override
	public void getLocation(long eventId, DbQueryCallback<LocationDTO> resultCallback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				LocationDTO locationDTO = locationDAO.select(eventId);
				resultCallback.processResult(locationDTO);
			}
		});
	}

	@Override
	public void getLocation(int id, DbQueryCallback<LocationDTO> resultCallback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				LocationDTO locationDTO = locationDAO.getLocation(id);
				resultCallback.processResult(locationDTO);
			}
		}).start();
	}

	@Override
	public void hasDetailLocation(long eventId, DbQueryCallback<Boolean> resultCallback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				int result = locationDAO.hasLocation(eventId);
				resultCallback.processResult(result == 1);
			}
		});
	}

	@Override
	public void addLocation(LocationDTO location, @Nullable DbQueryCallback<LocationDTO> resultCallback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				long id = locationDAO.insert(location);
				LocationDTO result = locationDAO.getLocation((int) id);
				if (resultCallback != null) {
					resultCallback.processResult(result);
				}
			}
		});
	}

	@Override
	public void removeLocation(long eventId, @Nullable DbQueryCallback<Boolean> resultCallback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				locationDAO.delete(eventId);
				if (resultCallback != null) {
					resultCallback.processResult(true);
				}
			}
		});
	}

	@Override
	public void modifyLocation(LocationDTO location, @androidx.annotation.Nullable DbQueryCallback<LocationDTO> resultCallback) {
		App.executorService.execute(new Runnable() {
			@Override
			public void run() {
				locationDAO.update(location);
				LocationDTO result = locationDAO.getLocation(location.getId());
				if (resultCallback != null) {
					resultCallback.processResult(result);
				}
			}
		});
	}
}
