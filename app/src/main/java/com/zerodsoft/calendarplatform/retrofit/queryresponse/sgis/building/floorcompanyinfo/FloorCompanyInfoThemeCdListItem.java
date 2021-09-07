package com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.building.floorcompanyinfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FloorCompanyInfoThemeCdListItem implements Parcelable
{
    @Expose
    @SerializedName("theme_cd")
    private String themeCd;

    @Expose
    @SerializedName("theme_cd_nm")
    private String themeCdNm;

    protected FloorCompanyInfoThemeCdListItem(Parcel in)
    {
        themeCd = in.readString();
        themeCdNm = in.readString();
    }

    public static final Creator<FloorCompanyInfoThemeCdListItem> CREATOR = new Creator<FloorCompanyInfoThemeCdListItem>()
    {
        @Override
        public FloorCompanyInfoThemeCdListItem createFromParcel(Parcel in)
        {
            return new FloorCompanyInfoThemeCdListItem(in);
        }

        @Override
        public FloorCompanyInfoThemeCdListItem[] newArray(int size)
        {
            return new FloorCompanyInfoThemeCdListItem[size];
        }
    };

    public String getThemeCd()
    {
        return themeCd;
    }

    public void setThemeCd(String themeCd)
    {
        this.themeCd = themeCd;
    }

    public String getThemeCdNm()
    {
        return themeCdNm;
    }

    public void setThemeCdNm(String themeCdNm)
    {
        this.themeCdNm = themeCdNm;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(themeCd);
        parcel.writeString(themeCdNm);
    }
}
