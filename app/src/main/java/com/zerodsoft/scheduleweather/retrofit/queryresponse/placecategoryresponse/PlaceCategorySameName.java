package com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceCategorySameName implements Parcelable
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

    protected PlaceCategorySameName(Parcel in)
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

    public static final Creator<PlaceCategorySameName> CREATOR = new Creator<PlaceCategorySameName>()
    {
        @Override
        public PlaceCategorySameName createFromParcel(Parcel in)
        {
            return new PlaceCategorySameName(in);
        }

        @Override
        public PlaceCategorySameName[] newArray(int size)
        {
            return new PlaceCategorySameName[size];
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
