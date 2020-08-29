package com.zerodsoft.scheduleweather.room.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationDTO implements Parcelable
{
    private String locName;
    private String wthName;
    private String locLat;
    private String locLon;
    private String locX;
    private String locY;

    public LocationDTO()
    {
    }

    protected LocationDTO(Parcel in)
    {
        locName = in.readString();
        wthName = in.readString();
        locLat = in.readString();
        locLon = in.readString();
        locX = in.readString();
        locY = in.readString();
    }

    public static final Creator<LocationDTO> CREATOR = new Creator<LocationDTO>()
    {
        @Override
        public LocationDTO createFromParcel(Parcel in)
        {
            return new LocationDTO(in);
        }

        @Override
        public LocationDTO[] newArray(int size)
        {
            return new LocationDTO[size];
        }
    };

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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(locName);
        parcel.writeString(wthName);
        parcel.writeString(locLat);
        parcel.writeString(locLon);
        parcel.writeString(locX);
        parcel.writeString(locY);
    }
}
