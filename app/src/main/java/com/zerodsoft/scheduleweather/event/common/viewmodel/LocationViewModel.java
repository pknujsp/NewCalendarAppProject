package com.zerodsoft.scheduleweather.event.common.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocationDao;
import com.zerodsoft.scheduleweather.event.common.repository.LocationRepository;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class LocationViewModel extends AndroidViewModel implements ILocationDao {
	private LocationRepository locationRepository;

	public LocationViewModel(@NonNull Application application) {
		super(application);
		locationRepository = new LocationRepository(application.getApplicationContext());
	}

	@Override
	public void getLocation(long eventId, DbQueryCallback<LocationDTO> resultCallback) {
		locationRepository.getLocation(eventId, resultCallback);
	}

	@Override
	public void getLocation(int id, DbQueryCallback<LocationDTO> resultCallback) {

	}

	@Override
	public void hasDetailLocation(long eventId, DbQueryCallback<Boolean> resultCallback) {
		locationRepository.hasDetailLocation(eventId, resultCallback);
	}

	@Override
	public void addLocation(LocationDTO location, DbQueryCallback<LocationDTO> resultCallback) {
		locationRepository.addLocation(location, resultCallback);
	}

	@Override
	public void removeLocation(long eventId, DbQueryCallback<Boolean> resultCallback) {
		locationRepository.removeLocation(eventId, resultCallback);
	}

	@Override
	public void modifyLocation(LocationDTO location, DbQueryCallback<LocationDTO> resultCallback) {
		locationRepository.modifyLocation(location, resultCallback);
	}

	@Override
	public void getAddressItem(LocalApiPlaceParameter parameter, JsonDownloader<AddressKakaoLocalResponse> callback) {
		locationRepository.getAddressItem(parameter, callback);
	}

	@Override
	public void getPlaceItem(LocalApiPlaceParameter parameter, String placeId, JsonDownloader<PlaceKakaoLocalResponse> callback) {
		locationRepository.getPlaceItem(parameter, placeId, callback);
	}
}
