package com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingattribute;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BuildingAttributeResult implements Parcelable
{
    @Expose
    @SerializedName("sufid")
    private String sufId;

    @Expose
    @SerializedName("bd_nm")
    private String bdName;

    @Expose
    @SerializedName("dev_figure_type")
    private String devFigureType;

    @Expose
    @SerializedName("bd_adm_addr")
    private String bdAdmAddr;

    @Expose
    @SerializedName("bd_naddr")
    private String bdNewAddress;

    @Expose
    @SerializedName("highest_flr")
    private String highestFloor;

    @Expose
    @SerializedName("lowest_flr")
    private String lowestFloor;

    protected BuildingAttributeResult(Parcel in)
    {
        sufId = in.readString();
        bdName = in.readString();
        devFigureType = in.readString();
        bdAdmAddr = in.readString();
        bdNewAddress = in.readString();
        highestFloor = in.readString();
        lowestFloor = in.readString();
    }

    public static final Creator<BuildingAttributeResult> CREATOR = new Creator<BuildingAttributeResult>()
    {
        @Override
        public BuildingAttributeResult createFromParcel(Parcel in)
        {
            return new BuildingAttributeResult(in);
        }

        @Override
        public BuildingAttributeResult[] newArray(int size)
        {
            return new BuildingAttributeResult[size];
        }
    };

    public String getSufId()
    {
        return sufId;
    }

    public void setSufId(String sufId)
    {
        this.sufId = sufId;
    }

    public String getBdName()
    {
        return bdName;
    }

    public void setBdName(String bdName)
    {
        this.bdName = bdName;
    }

    public String getDevFigureType()
    {
        return devFigureType;
    }

    public void setDevFigureType(String devFigureType)
    {
        this.devFigureType = devFigureType;
    }

    public String getBdAdmAddr()
    {
        return bdAdmAddr;
    }

    public void setBdAdmAddr(String bdAdmAddr)
    {
        this.bdAdmAddr = bdAdmAddr;
    }

    public String getBdNewAddress()
    {
        return bdNewAddress;
    }

    public void setBdNewAddress(String bdNewAddress)
    {
        this.bdNewAddress = bdNewAddress;
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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(sufId);
        parcel.writeString(bdName);
        parcel.writeString(devFigureType);
        parcel.writeString(bdAdmAddr);
        parcel.writeString(bdNewAddress);
        parcel.writeString(highestFloor);
        parcel.writeString(lowestFloor);
    }
}
