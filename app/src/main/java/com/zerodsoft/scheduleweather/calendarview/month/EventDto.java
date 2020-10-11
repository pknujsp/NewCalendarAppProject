package com.zerodsoft.scheduleweather.calendarview.month;

import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventDto
{
    public Date date;
    public List<ScheduleDTO> schedulesList;

    public EventDto(Date date)
    {
        this.date = date;
        this.schedulesList = new ArrayList<>();
    }
}
