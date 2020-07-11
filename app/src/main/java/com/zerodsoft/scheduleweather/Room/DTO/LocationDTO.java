package com.zerodsoft.scheduleweather.Room.DTO;

import androidx.room.ColumnInfo;

import java.io.Serializable;

public class LocationDTO implements Serializable
{
    private String locName;
    private String wthName;
    private String locLat;
    private String locLon;
    private String locX;
    private String locY;

    public String getLocName()
    {
        return locName;
    }

    public LocationDTO setLocName(String locName)
    {
        this.locName = locName;
        return this;
    }

    public String getWthName()
    {
        return wthName;
    }

    public LocationDTO setWthName(String wthName)
    {
        this.wthName = wthName;
        return this;
    }

    public String getLocLat()
    {
        return locLat;
    }

    public LocationDTO setLocLat(String locLat)
    {
        this.locLat = locLat;
        return this;
    }

    public String getLocLon()
    {
        return locLon;
    }

    public LocationDTO setLocLon(String locLon)
    {
        this.locLon = locLon;
        return this;
    }

    public String getLocX()
    {
        return locX;
    }

    public LocationDTO setLocX(String locX)
    {
        this.locX = locX;
        return this;
    }

    public String getLocY()
    {
        return locY;
    }

    public LocationDTO setLocY(String locY)
    {
        this.locY = locY;
        return this;
    }
}
