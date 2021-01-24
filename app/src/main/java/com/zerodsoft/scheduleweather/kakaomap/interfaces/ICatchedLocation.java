package com.zerodsoft.scheduleweather.kakaomap.interfaces;

import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public interface ICatchedLocation
{

    LocationDTO getLocation();

    void choiceLocation(String location);

    void choiceLocation(LocationDTO locationDTO);
}
