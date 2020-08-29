package com.zerodsoft.scheduleweather.utility;

public class LonLat
{
    private double longitude;
    private double latitude;
    private int x;
    private int y;

    public double getLongitude()
    {
        return longitude;
    }

    public LonLat setLongitude(double longitude)
    {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public LonLat setLatitude(double latitude)
    {
        this.latitude = latitude;
        return this;
    }

    public int getX()
    {
        return x;
    }

    public LonLat setX(int x)
    {
        this.x = x;
        return this;
    }

    public int getY()
    {
        return y;
    }

    public LonLat setY(int y)
    {
        this.y = y;
        return this;
    }
}