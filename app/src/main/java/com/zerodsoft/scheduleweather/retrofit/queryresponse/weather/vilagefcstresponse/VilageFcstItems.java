package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.vilagefcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.WeatherItems;

import java.util.List;

public class VilageFcstItems extends WeatherItems implements Parcelable
{
    @Expose
    @SerializedName("item")
    private List<VilageFcstItem> item;

    protected VilageFcstItems(Parcel in)
    {
        item = in.createTypedArrayList(VilageFcstItem.CREATOR);
    }

    public static final Creator<VilageFcstItems> CREATOR = new Creator<VilageFcstItems>()
    {
        @Override
        public VilageFcstItems createFromParcel(Parcel in)
        {
            return new VilageFcstItems(in);
        }

        @Override
        public VilageFcstItems[] newArray(int size)
        {
            return new VilageFcstItems[size];
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

    public void setItem(List<VilageFcstItem> item)
    {
        this.item = item;
    }

    public List<VilageFcstItem> getItem()
    {
        return item;
    }
}
