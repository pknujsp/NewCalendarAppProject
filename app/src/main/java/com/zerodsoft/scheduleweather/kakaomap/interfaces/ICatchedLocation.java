package com.zerodsoft.scheduleweather.kakaomap.interfaces;

import com.google.android.gms.location.LocationRequest;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

public interface ICatchedLocation
{
    AddressDTO getAddress();

    PlaceDTO getPlace();

    LocationDTO getLocation();

    void choiceLocation(String location);

    void choiceLocation(LocationDTO locationDTO);
}
