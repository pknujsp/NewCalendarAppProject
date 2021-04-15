package com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.floorcompanyinfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FloorCompanyInfoCompanyListItem implements Parcelable
{
    @Expose
    @SerializedName("center_x")
    private String centerX;

    @Expose
    @SerializedName("center_y")
    private String centerY;

    @Expose
    @SerializedName("corp_nm")
    private String corpName;

    @Expose
    @SerializedName("decilist_serial")
    private String deciListSerial;

    @Expose
    @SerializedName("theme_cd")
    private String themeCd;


    protected FloorCompanyInfoCompanyListItem(Parcel in)
    {
        centerX = in.readString();
        centerY = in.readString();
        corpName = in.readString();
        deciListSerial = in.readString();
        themeCd = in.readString();
    }

    public static final Creator<FloorCompanyInfoCompanyListItem> CREATOR = new Creator<FloorCompanyInfoCompanyListItem>()
    {
        @Override
        public FloorCompanyInfoCompanyListItem createFromParcel(Parcel in)
        {
            return new FloorCompanyInfoCompanyListItem(in);
        }

        @Override
        public FloorCompanyInfoCompanyListItem[] newArray(int size)
        {
            return new FloorCompanyInfoCompanyListItem[size];
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
        parcel.writeString(centerX);
        parcel.writeString(centerY);
        parcel.writeString(corpName);
        parcel.writeString(deciListSerial);
        parcel.writeString(themeCd);
    }

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

    public String getCorpName()
    {
        return corpName;
    }

    public void setCorpName(String corpName)
    {
        this.corpName = corpName;
    }

    public String getDeciListSerial()
    {
        return deciListSerial;
    }

    public void setDeciListSerial(String deciListSerial)
    {
        this.deciListSerial = deciListSerial;
    }

    public String getThemeCd()
    {
        return themeCd;
    }

    public void setThemeCd(String themeCd)
    {
        this.themeCd = themeCd;
    }
}
