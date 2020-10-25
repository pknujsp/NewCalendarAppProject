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

    public UltraSrtNcstItem setBaseDate(String baseDate)
    {
        this.baseDate = baseDate;
        return this;
    }

    public String getBaseTime()
    {
        return baseTime;
    }

    public UltraSrtNcstItem setBaseTime(String baseTime)
    {
        this.baseTime = baseTime;
        return this;
    }

    public String getCategory()
    {
        return category;
    }

    public UltraSrtNcstItem setCategory(String category)
    {
        this.category = category;
        return this;
    }

    public String getNx()
    {
        return nx;
    }

    public UltraSrtNcstItem setNx(String nx)
    {
        this.nx = nx;
        return this;
    }

    public String getNy()
    {
        return ny;
    }

    public UltraSrtNcstItem setNy(String ny)
    {
        this.ny = ny;
        return this;
    }

    public String getObsrValue()
    {
        return obsrValue;
    }

    public UltraSrtNcstItem setObsrValue(String obsrValue)
    {
        this.obsrValue = obsrValue;
        return this;
    }
}
