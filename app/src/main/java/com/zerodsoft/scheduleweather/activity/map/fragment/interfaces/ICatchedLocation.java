package com.zerodsoft.scheduleweather.activity.map.fragment.interfaces;

import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.LocationDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;

public interface ICatchedLocation
{
    LocationDTO getLocation();

    PlaceDTO getPlace();

    AddressDTO getAddress();
}
