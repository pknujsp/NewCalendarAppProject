package com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UltraSrtFcstItem implements Parcelable
{
    // 초단기예보 아이템
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
    @SerializedName("fcstDate")
    private String fcstDate;

    @Expose
    @SerializedName("fcstTime")
    private String fcstTime;

    @Expose
    @SerializedName("fcstValue")
    private String fcstValue;

    @Expose
    @SerializedName("nx")
    private String nx;

    @Expose
    @SerializedName("ny")
    private String ny;


    protected UltraSrtFcstItem(Parcel in)
    {
        baseDate = in.readString();
        baseTime = in.readString();
        category = in.readString();
        fcstDate = in.readString();
        fcstTime = in.readString();
        fcstValue = in.readString();
        nx = in.readString();
        ny = in.readString();
    }

    public static final Creator<UltraSrtFcstItem> CREATOR = new Creator<UltraSrtFcstItem>()
    {
        @Override
        public UltraSrtFcstItem createFromParcel(Parcel in)
        {
            return new UltraSrtFcstItem(in);
        }

        @Override
        public UltraSrtFcstItem[] newArray(int size)
        {
            return new UltraSrtFcstItem[size];
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
        parcel.writeString(fcstDate);
        parcel.writeString(fcstTime);
        parcel.writeString(fcstValue);
        parcel.writeString(nx);
        parcel.writeString(ny);
    }

    public String getBaseDate()
    {
        return baseDate;
    }

    public UltraSrtFcstItem setBaseDate(String baseDate)
    {
        this.baseDate = baseDate;
        return this;
    }

    public String getBaseTime()
    {
        return baseTime;
    }

    public UltraSrtFcstItem setBaseTime(String baseTime)
    {
        this.baseTime = baseTime;
        return this;
    }

    public String getCategory()
    {
        return category;
    }

    public UltraSrtFcstItem setCategory(String category)
    {
        this.category = category;
        return this;
    }

    public String getFcstDate()
    {
        return fcstDate;
    }

    public UltraSrtFcstItem setFcstDate(String fcstDate)
    {
        this.fcstDate = fcstDate;
        return this;
    }

    public String getFcstTime()
    {
        return fcstTime;
    }

    public UltraSrtFcstItem setFcstTime(String fcstTime)
    {
        this.fcstTime = fcstTime;
        return this;
    }

    public String getFcstValue()
    {
        return fcstValue;
    }

    public UltraSrtFcstItem setFcstValue(String fcstValue)
    {
        this.fcstValue = fcstValue;
        return this;
    }

    public String getNx()
    {
        return nx;
    }

    public UltraSrtFcstItem setNx(String nx)
    {
        this.nx = nx;
        return this;
    }

    public String getNy()
    {
        return ny;
    }

    public UltraSrtFcstItem setNy(String ny)
    {
        this.ny = ny;
        return this;
    }
}
