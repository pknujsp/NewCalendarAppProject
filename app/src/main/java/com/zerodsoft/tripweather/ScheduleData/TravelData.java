package com.zerodsoft.tripweather.ScheduleData;

public class TravelData
{
    private String travelName;
    private String period;
    private int travelId;

    public void setTravelId(int travelId)
    {
        this.travelId = travelId;
    }

    public int getTravelId()
    {
        return travelId;
    }

    public String getTravelName()
    {
        return travelName;
    }

    public void setTravelName(String travelName)
    {
        this.travelName = travelName;
    }

    public String getPeriod()
    {
        return period;
    }

    public void setPeriod(String period)
    {
        this.period = period;
    }


}
