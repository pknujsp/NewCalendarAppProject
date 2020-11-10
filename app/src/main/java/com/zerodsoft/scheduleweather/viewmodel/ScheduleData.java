package com.zerodsoft.scheduleweather.viewmodel;

import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.List;

public class ScheduleData
{
    private ScheduleDTO schedule;
    private List<AddressDTO> addresses;
    private List<PlaceDTO> places;

    public ScheduleData()
    {
    }


    public void setSchedule(ScheduleDTO schedule)
    {
        this.schedule = schedule;
    }

    public ScheduleDTO getSchedule()
    {
        return schedule;
    }

    public List<AddressDTO> getAddresses()
    {
        return addresses;
    }

    public void setAddresses(List<AddressDTO> addresses)
    {
        this.addresses = addresses;
    }

    public List<PlaceDTO> getPlaces()
    {
        return places;
    }

    public void setPlaces(List<PlaceDTO> places)
    {
        this.places = places;
    }
}
