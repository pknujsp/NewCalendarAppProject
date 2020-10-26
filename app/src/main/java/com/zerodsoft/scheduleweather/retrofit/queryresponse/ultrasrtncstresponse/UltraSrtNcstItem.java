package com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UltraSrtNcstItem implements Parcelable
{
    // 초단기실황조회 아이템
    @Expose
    @SerializedName("baseDate")
    private String baseDate;

    @Expose
    @SerializedName("baseTime")
    private String baseTime;

    @Expose
    @SerializedName("category")
    private String category;

    @Expose
    @SerializedName("nx")
    private String nx;

    @Expose
    @SerializedName("ny")
    private String ny;

    @Expose
    @SerializedName("obsrValue")
    private String obsrValue;

    protected UltraSrtNcstItem(Parcel in)
    {
        baseDate = in.readString();
        baseTime = in.readString();
        category = in.readString();
        nx = in.readString();
        ny = in.readString();
        obsrValue = in.readString();
    }

    public static final Creator<UltraSrtNcstItem> CREATOR = new Creator<UltraSrtNcstItem>()
    {
        @Override
        public UltraSrtNcstItem createFromParcel(Parcel in)
        {
            return new UltraSrtNcstItem(in);
        }

        @Override
        public UltraSrtNcstItem[] newArray(int size)
        {
            return new UltraSrtNcstItem[size];
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
        parcel.writeString(baseDate);
        parcel.writeString(baseTime);
        parcel.writeString(category);
        parcel.writeString(nx);
        parcel.writeString(ny);
        parcel.writeString(obsrValue);
    }

    public String getBaseDate()
    {
        return baseDate;
    }

    public void setBaseDate(String baseDate)
    {
        this.baseDate = baseDate;
    }

    public String getBaseTime()
    {
        return baseTime;
    }

    public void setBaseTime(String baseTime)
    {
        this.baseTime = baseTime;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getNx()
    {
        return nx;
    }

    public void setNx(String nx)
    {
        this.nx = nx;
    }

    public String getNy()
    {
        return ny;
    }

    public void setNy(String ny)
    {
        this.ny = ny;
    }

    public String getObsrValue()
    {
        return obsrValue;
    }

    public void setObsrValue(String obsrValue)
    {
        this.obsrValue = obsrValue;
    }
}
