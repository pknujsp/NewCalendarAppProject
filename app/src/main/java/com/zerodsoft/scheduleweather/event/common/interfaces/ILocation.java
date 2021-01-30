package com.zerodsoft.scheduleweather.event.common.interfaces;

import com.zerodsoft.scheduleweather.room.dto.LocationDTO;

public interface ILocation
{
    void showRequestLocDialog();

    LocationDTO getLocation();

    boolean hasSimpleLocation();

    boolean hasDetailLocation();
}
