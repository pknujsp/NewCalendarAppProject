package com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.floorcompanyinfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FloorCompanyInfoFacilityListItem implements Parcelable
{
    @Expose
    @SerializedName("center_x")
    private String centerX;

    @Expose
    @SerializedName("center_y")
    private String centerY;

    @Expose
    @SerializedName("fac_type")
    private String facType;

    protected FloorCompanyInfoFacilityListItem(Parcel in)
    {
        centerX = in.readString();
        centerY = in.readString();
        facType = in.readString();
    }

    public static final Creator<FloorCompanyInfoFacilityListItem> CREATOR = new Creator<FloorCompanyInfoFacilityListItem>()
    {
        @Override
        public FloorCompanyInfoFacilityListItem createFromParcel(Parcel in)
        {
            return new FloorCompanyInfoFacilityListItem(in);
        }

        @Override
        public FloorCompanyInfoFacilityListItem[] newArray(int size)
        {
            return new FloorCompanyInfoFacilityListItem[size];
        }
    };

    public String getCenterX()
    {
        return centerX;
    }

    public void setCenterX(String centerX)
    {
        this.centerX = centerX;
    }

    public String getCenterY()
    {
        return centerY;
    }

    public void setCenterY(String centerY)
    {
        this.centerY = centerY;
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
        parcel.writeString(centerX);
        parcel.writeString(centerY);
        parcel.writeString(facType);
    }
}
