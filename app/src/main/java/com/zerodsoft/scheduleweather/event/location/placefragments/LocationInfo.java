package com.zerodsoft.scheduleweather.event.location.placefragments;

public class LocationInfo
{
    private final double latitude;
    private final double longitude;
    private final String locationName;

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
