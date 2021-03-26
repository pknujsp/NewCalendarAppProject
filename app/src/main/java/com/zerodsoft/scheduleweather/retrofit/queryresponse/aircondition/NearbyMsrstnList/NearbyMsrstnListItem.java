package com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NearbyMsrstnListItem implements Parcelable
{
    @Expose
    @SerializedName("stationName")
    private String stationName;

    @Expose
    @SerializedName("addr")
    private String addr;

    @Expose
    @SerializedName("tm")
    private String tm;


    protected NearbyMsrstnListItem(Parcel in)
    {
        stationName = in.readString();
        addr = in.readString();
        tm = in.readString();
    }

    public static final Creator<NearbyMsrstnListItem> CREATOR = new Creator<NearbyMsrstnListItem>()
    {
        @Override
        public NearbyMsrstnListItem createFromParcel(Parcel in)
        {
            return new NearbyMsrstnListItem(in);
        }

        @Override
        public NearbyMsrstnListItem[] newArray(int size)
        {
            return new NearbyMsrstnListItem[size];
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
        parcel.writeString(stationName);
        parcel.writeString(addr);
        parcel.writeString(tm);
    }

    public String getStationName()
    {
        return stationName;
    }

    public void setStationName(String stationName)
    {
        this.stationName = stationName;
    }

    public String getAddr()
    {
        return addr;
    }

    public void setAddr(String addr)
    {
        this.addr = addr;
    }

    public String getTm()
    {
        return tm;
    }

    public void setTm(String tm)
    {
        this.tm = tm;
    }
}
