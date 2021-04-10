package com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.floorcompanyinfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FloorCompanyInfoResult implements Parcelable
{
    @Expose
    @SerializedName("facilitylist")
    private List<FloorCompanyInfoFacilityListItem> facilityList;

    @Expose
    @SerializedName("companylist")
    private List<FloorCompanyInfoCompanyListItem> companyList;

    @Expose
    @SerializedName("theme_cd_list")
    private List<FloorCompanyInfoThemeCdListItem> themeCdList;

    protected FloorCompanyInfoResult(Parcel in)
    {
        facilityList = in.createTypedArrayList(FloorCompanyInfoFacilityListItem.CREATOR);
        companyList = in.createTypedArrayList(FloorCompanyInfoCompanyListItem.CREATOR);
        themeCdList = in.createTypedArrayList(FloorCompanyInfoThemeCdListItem.CREATOR);
    }

    public static final Creator<FloorCompanyInfoResult> CREATOR = new Creator<FloorCompanyInfoResult>()
    {
        @Override
        public FloorCompanyInfoResult createFromParcel(Parcel in)
        {
            return new FloorCompanyInfoResult(in);
        }

        @Override
        public FloorCompanyInfoResult[] newArray(int size)
        {
            return new FloorCompanyInfoResult[size];
        }
    };

    public List<FloorCompanyInfoFacilityListItem> getFacilityList()
    {
        return facilityList;
    }

    public void setFacilityList(List<FloorCompanyInfoFacilityListItem> facilityList)
    {
        this.facilityList = facilityList;
    }

    public List<FloorCompanyInfoCompanyListItem> getCompanyList()
    {
        return companyList;
    }

    public void setCompanyList(List<FloorCompanyInfoCompanyListItem> companyList)
    {
        this.companyList = companyList;
    }

    public List<FloorCompanyInfoThemeCdListItem> getThemeCdList()
    {
        return themeCdList;
    }

    public void setThemeCdList(List<FloorCompanyInfoThemeCdListItem> themeCdList)
    {
        this.themeCdList = themeCdList;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeTypedList(facilityList);
        parcel.writeTypedList(companyList);
        parcel.writeTypedList(themeCdList);
    }
}
