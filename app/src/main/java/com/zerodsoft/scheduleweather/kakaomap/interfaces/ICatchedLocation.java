package com.zerodsoft.scheduleweather.kakaomap.interfaces;

import com.google.android.gms.location.LocationRequest;
import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

public interface ICatchedLocation
{
    LocationDTO getLocation();

    PlaceDTO getPlace();

    AddressDTO getAddress();

    void choiceLocation(LocationDTO locationDTO);
}
