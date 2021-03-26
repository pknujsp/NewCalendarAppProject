package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.WeatherItems;

import java.util.List;

public class UltraSrtFcstItems extends WeatherItems implements Parcelable
{
    @Expose
    @SerializedName("item")
    private List<UltraSrtFcstItem> item;

    protected UltraSrtFcstItems(Parcel in)
    {
        item = in.createTypedArrayList(UltraSrtFcstItem.CREATOR);
    }

    public static final Creator<UltraSrtFcstItems> CREATOR = new Creator<UltraSrtFcstItems>()
    {
        @Override
        public UltraSrtFcstItems createFromParcel(Parcel in)
        {
            return new UltraSrtFcstItems(in);
        }

        @Override
        public UltraSrtFcstItems[] newArray(int size)
        {
            return new UltraSrtFcstItems[size];
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
        parcel.writeTypedList(item);
    }

    public void setItem(List<UltraSrtFcstItem> item)
    {
        this.item = item;
    }

    public List<UltraSrtFcstItem> getItem()
    {
        return item;
    }
}
