package com.zerodsoft.tripweather.ScheduleData;

import com.zerodsoft.tripweather.Calendar.SelectedDate;
import com.zerodsoft.tripweather.Room.DTO.Area;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class TravelSchedule implements Serializable
{
    private SelectedDate startDate;
    private SelectedDate endDate;
    private Area travelDestination;
    private String areaName;
    private String startDateStr;
    private String endDateStr;

    public String getStartDateStr()
    {
        return startDateStr;
    }

    public void setStartDateStr(String startDateStr)
    {
        this.startDateStr = startDateStr;
    }

    public String getEndDateStr()
    {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr)
    {
        this.endDateStr = endDateStr;
    }

    public void setAreaName(String areaName)
    {
        this.areaName = areaName;
    }

    public String getAreaName()
    {
        return areaName;
    }

    public SelectedDate getStartDate()
    {
        return startDate;
    }

    public void setStartDate(SelectedDate startDate)
    {
        this.startDate = startDate;
    }

    public SelectedDate getEndDate()
    {
        return endDate;
    }

    public void setEndDate(SelectedDate endDate)
    {
        this.endDate = endDate;
    }

    public Area getTravelDestination()
    {
        return travelDestination;
    }

    public void setTravelDestination(Area travelDestination)
    {
        this.travelDestination = travelDestination;
    }
}
