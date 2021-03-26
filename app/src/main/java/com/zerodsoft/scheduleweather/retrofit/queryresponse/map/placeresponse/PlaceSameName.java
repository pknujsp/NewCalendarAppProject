package com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceSameName implements Parcelable
{
    @SerializedName("region")
    @Expose
    private String[] region;

    @SerializedName("keyword")
    @Expose
    private String keyword;

    @SerializedName("selected_region")
    @Expose
    private String selectedRegion;

    protected PlaceSameName(Parcel in)
    {
        region = in.createStringArray();
        keyword = in.readString();
        selectedRegion = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeStringArray(region);
        dest.writeString(keyword);
        dest.writeString(selectedRegion);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<PlaceSameName> CREATOR = new Creator<PlaceSameName>()
    {
        @Override
        public PlaceSameName createFromParcel(Parcel in)
        {
            return new PlaceSameName(in);
        }

        @Override
        public PlaceSameName[] newArray(int size)
        {
            return new PlaceSameName[size];
        }
    };

    public String[] getRegion()
    {
        return region;
    }

    public void setRegion(String[] region)
    {
        this.region = region;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public String getSelectedRegion()
    {
        return selectedRegion;
    }

    public void setSelectedRegion(String selectedRegion)
    {
        this.selectedRegion = selectedRegion;
    }
}
