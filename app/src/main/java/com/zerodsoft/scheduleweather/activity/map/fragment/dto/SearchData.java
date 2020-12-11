package com.zerodsoft.scheduleweather.activity.map.fragment.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.zerodsoft.scheduleweather.retrofit.paremeters.LocalApiPlaceParameter;

public class SearchData implements Parcelable
{
    private final String searchWord;
    private final double latitude;
    private final double longitude;

    public SearchData(String searchWord, double latitude, double longitude)
    {
        this.searchWord = searchWord;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected SearchData(Parcel in)
    {
        searchWord = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<SearchData> CREATOR = new Creator<SearchData>()
    {
        @Override
        public SearchData createFromParcel(Parcel in)
        {
            return new SearchData(in);
        }

        @Override
        public SearchData[] newArray(int size)
        {
            return new SearchData[size];
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
        parcel.writeString(searchWord);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    public String getSearchWord()
    {
        return searchWord;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }
}
