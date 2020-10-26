package com.zerodsoft.scheduleweather.viewmodel;

import com.zerodsoft.scheduleweather.room.dto.AddressDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceDTO;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

public class ScheduleData
{
    private ScheduleDTO schedule;
    private AddressDTO address;
    private PlaceDTO place;

    public ScheduleData()
    {
    }

    public void setAddress(AddressDTO address)
    {
        this.address = address;
    }

    public void setPlace(PlaceDTO place)
    {
        this.place = place;
    }

    public void setSchedule(ScheduleDTO schedule)
    {
        this.schedule = schedule;
    }

    public ScheduleDTO getSchedule()
    {
        return schedule;
    }

    public AddressDTO getAddress()
    {
        return address;
    }

    public PlaceDTO getPlace()
    {
        return place;
    }
}
