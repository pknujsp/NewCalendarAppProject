package com.zerodsoft.scheduleweather.Room.DTO;

public class FavoriteLocDTO
{
    private int id;
    private int scheduleId;
    private String locName;
    private String locCategory;
    private String locLat;
    private String locLon;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getScheduleId()
    {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId)
    {
        this.scheduleId = scheduleId;
    }

    public String getLocName()
    {
        return locName;
    }

    public void setLocName(String locName)
    {
        this.locName = locName;
    }

    public String getLocCategory()
    {
        return locCategory;
    }

    public void setLocCategory(String locCategory)
    {
        this.locCategory = locCategory;
    }

    public String getLocLat()
    {
        return locLat;
    }

    public void setLocLat(String locLat)
    {
        this.locLat = locLat;
    }

    public String getLocLon()
    {
        return locLon;
    }

    public void setLocLon(String locLon)
    {
        this.locLon = locLon;
    }
}
