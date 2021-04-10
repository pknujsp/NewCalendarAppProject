package com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingarea;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BuildingAreaItem implements Parcelable
{
    @Expose
    @SerializedName("bd_nm")
    private String bdName;

    @Expose
    @SerializedName("sufid")
    private String sufId;

    @Expose
    @SerializedName("highest_flr")
    private String highestFloor;

    @Expose
    @SerializedName("lowest_flr")
    private String lowestFloor;

    @Expose
    @SerializedName("bd_naddr")
    private String bdNewAddress;

    protected BuildingAreaItem(Parcel in)
    {
        bdName = in.readString();
        sufId = in.readString();
        highestFloor = in.readString();
        lowestFloor = in.readString();
        bdNewAddress = in.readString();
    }

    public static final Creator<BuildingAreaItem> CREATOR = new Creator<BuildingAreaItem>()
    {
        @Override
        public BuildingAreaItem createFromParcel(Parcel in)
        {
            return new BuildingAreaItem(in);
        }

        @Override
        public BuildingAreaItem[] newArray(int size)
        {
            return new BuildingAreaItem[size];
        }
    };

    public String getBdName()
    {
        return bdName;
    }

    public void setBdName(String bdName)
    {
        this.bdName = bdName;
    }

    public String getSufId()
    {
        return sufId;
    }

    public void setSufId(String sufId)
    {
        this.sufId = sufId;
    }

    public String getHighestFloor()
    {
        return highestFloor;
    }

    public void setHighestFloor(String highestFloor)
    {
        this.highestFloor = highestFloor;
    }

    public String getLowestFloor()
    {
        return lowestFloor;
    }

    public void setLowestFloor(String lowestFloor)
    {
        this.lowestFloor = lowestFloor;
    }

    public String getBdNewAddress()
    {
        return bdNewAddress;
    }

    public void setBdNewAddress(String bdNewAddress)
    {
        this.bdNewAddress = bdNewAddress;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(bdName);
        parcel.writeString(sufId);
        parcel.writeString(highestFloor);
        parcel.writeString(lowestFloor);
        parcel.writeString(bdNewAddress);
    }
}
