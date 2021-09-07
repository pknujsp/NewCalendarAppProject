package com.zerodsoft.calendarplatform.utility;

public class LonLat
{
    private double longitude;
    private double latitude;
    private int x;
    private int y;
    private int longitude_degree;
    private int longitude_minutes;
    private double longitude_seconds;

    private int latitude_degree;
    private int latitude_minutes;
    private double latitude_seconds;

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

    public int getLongitude_degree()
    {
        return longitude_degree;
    }

    public LonLat setLongitude_degree(int longitude_degree)
    {
        this.longitude_degree = longitude_degree;
        return this;
    }

    public int getLongitude_minutes()
    {
        return longitude_minutes;
    }

    public LonLat setLongitude_minutes(int longitude_minutes)
    {
        this.longitude_minutes = longitude_minutes;
        return this;
    }

    public double getLongitude_seconds()
    {
        return longitude_seconds;
    }

    public LonLat setLongitude_seconds(double longitude_seconds)
    {
        this.longitude_seconds = longitude_seconds;
        return this;
    }

    public int getLatitude_degree()
    {
        return latitude_degree;
    }

    public LonLat setLatitude_degree(int latitude_degree)
    {
        this.latitude_degree = latitude_degree;
        return this;
    }

    public int getLatitude_minutes()
    {
        return latitude_minutes;
    }

    public LonLat setLatitude_minutes(int latitude_minutes)
    {
        this.latitude_minutes = latitude_minutes;
        return this;
    }

    public double getLatitude_seconds()
    {
        return latitude_seconds;
    }

    public LonLat setLatitude_seconds(double latitude_seconds)
    {
        this.latitude_seconds = latitude_seconds;
        return this;
    }
}