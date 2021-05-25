package com.zerodsoft.scheduleweather.event.common.viewmodel;

import android.app.Application;
import android.service.carrier.CarrierMessagingService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.common.interfaces.ILocationDao;
import com.zerodsoft.scheduleweather.event.common.repository.LocationRepository;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public class LocationViewModel extends AndroidViewModel implements ILocationDao {
	private MutableLiveData<LocationDTO> locationLiveData;
	private LocationRepository locationRepository;

	public LocationViewModel(@NonNull Application application) {
		super(application);
		locationRepository = new LocationRepository(application.getApplicationContext());
		locationLiveData = locationRepository.getLocationLiveData();
	}

	@Override
	public void getLocation(int calendarId, long eventId, DbQueryCallback<LocationDTO> resultCallback) {
		locationRepository.getLocation(calendarId, eventId, resultCallback);
	}

	@Override
	public void hasDetailLocation(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> resultCallback) {
		locationRepository.hasDetailLocation(calendarId, eventId, resultCallback);
	}

	@Override
	public void addLocation(LocationDTO location, CarrierMessagingService.ResultCallback<Boolean> resultCallback) {
		locationRepository.addLocation(location, resultCallback);
	}

	@Override
	public void removeLocation(int calendarId, long eventId, CarrierMessagingService.ResultCallback<Boolean> resultCallback) {
		locationRepository.removeLocation(calendarId, eventId, resultCallback);
	}

	@Override
	public void modifyLocation(LocationDTO location, CarrierMessagingService.ResultCallback<Boolean> resultCallback) {
		locationRepository.modifyLocation(location, resultCallback);
	}

	@Override
	public void getAddressItem(LocalApiPlaceParameter parameter, CarrierMessagingService.ResultCallback<DataWrapper<AddressResponseDocuments>> callback) {
		locationRepository.getAddressItem(parameter, callback);
	}

	@Override
	public void getPlaceItem(LocalApiPlaceParameter parameter, String placeId, JsonDownloader<PlaceKakaoLocalResponse> callback) {
		locationRepository.getPlaceItem(parameter, placeId, callback);
	}

	public MutableLiveData<LocationDTO> getLocationLiveData() {
		return locationLiveData;
	}
}
