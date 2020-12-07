package com.zerodsoft.scheduleweather.room.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationDTO implements Parcelable
{
    public LocationDTO()
    {
    }

    protected LocationDTO(Parcel in)
    {
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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
    }
}
