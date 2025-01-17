package com.zerodsoft.calendarplatform.event.common.interfaces;

import com.zerodsoft.calendarplatform.common.classes.JsonDownloader;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.retrofit.paremeters.LocalApiPlaceParameter;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.addressresponse.AddressKakaoLocalResponse;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.map.placeresponse.PlaceKakaoLocalResponse;
import com.zerodsoft.calendarplatform.room.dto.LocationDTO;

import org.jetbrains.annotations.Nullable;

public interface ILocationDao {
	void getLocation(long eventId, DbQueryCallback<LocationDTO> resultCallback);

	void getLocation(int id, DbQueryCallback<LocationDTO> resultCallback);

	void hasDetailLocation(long eventId, DbQueryCallback<Boolean> resultCallback);

	void addLocation(LocationDTO location, @Nullable DbQueryCallback<LocationDTO> resultCallback);

	void removeLocation(long eventId, @Nullable DbQueryCallback<Boolean> resultCallback);

	void modifyLocation(LocationDTO location, DbQueryCallback<LocationDTO> resultCallback);

	void getAddressItem(LocalApiPlaceParameter parameter, JsonDownloader<AddressKakaoLocalResponse> callback);

	void getPlaceItem(LocalApiPlaceParameter parameter, String placeId, JsonDownloader<PlaceKakaoLocalResponse> callback);
}