package com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.building.flooretcfacility;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FloorEtcFacilityItem implements Parcelable
{
    @Expose
    @SerializedName("figure_type")
    private String figureType;

    @Expose
    @SerializedName("fac_type")
    private String facType;

    protected FloorEtcFacilityItem(Parcel in)
    {
        figureType = in.readString();
        facType = in.readString();
    }

    public static final Creator<FloorEtcFacilityItem> CREATOR = new Creator<FloorEtcFacilityItem>()
    {
        @Override
        public FloorEtcFacilityItem createFromParcel(Parcel in)
        {
            return new FloorEtcFacilityItem(in);
        }

        @Override
        public FloorEtcFacilityItem[] newArray(int size)
        {
            return new FloorEtcFacilityItem[size];
        }
    };

    public String getFigureType()
    {
        return figureType;
    }

    public void setFigureType(String figureType)
    {
        this.figureType = figureType;
    }

    public String getFacType()
    {
        return facType;
    }

    public void setFacType(String facType)
    {
        this.facType = facType;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(figureType);
        parcel.writeString(facType);
    }
}
