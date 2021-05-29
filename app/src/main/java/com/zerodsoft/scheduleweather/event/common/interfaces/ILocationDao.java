package com.zerodsoft.scheduleweather.event.common.interfaces;

import com.zerodsoft.scheduleweather.common.classes.JsonDownloader;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public interface ILocationDao {
	public void getLocation(long eventId, DbQueryCallback<LocationDTO> resultCallback);

	public void getLocation(int id, DbQueryCallback<LocationDTO> resultCallback);

	public void hasDetailLocation(long eventId, DbQueryCallback<Boolean> resultCallback);

	public void addLocation(LocationDTO location, DbQueryCallback<LocationDTO> resultCallback);

	public void removeLocation(long eventId, DbQueryCallback<Boolean> resultCallback);

	public void modifyLocation(LocationDTO location, DbQueryCallback<LocationDTO> resultCallback);

	public void getAddressItem(LocalApiPlaceParameter parameter, JsonDownloader<AddressKakaoLocalResponse> callback);

	public void getPlaceItem(LocalApiPlaceParameter parameter, String placeId, JsonDownloader<PlaceKakaoLocalResponse> callback);
}