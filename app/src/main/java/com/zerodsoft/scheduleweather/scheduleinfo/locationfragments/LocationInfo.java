package com.zerodsoft.scheduleweather.scheduleinfo.locationfragments;

public class LocationInfo
{
    private double latitude;
    private double longitude;
    private String locationName;

    public LocationInfo(double latitude, double longitude, String locationName)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public String getLocationName()
    {
        return locationName;
    }

    public LocationInfo copy()
    {
        return new LocationInfo(latitude, longitude, locationName);
    }
}
