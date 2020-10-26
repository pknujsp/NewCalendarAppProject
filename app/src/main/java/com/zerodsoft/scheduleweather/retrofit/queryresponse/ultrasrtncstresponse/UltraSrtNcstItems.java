package com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UltraSrtNcstItems implements Parcelable
{
    @Expose
    @SerializedName("item")
    private List<UltraSrtNcstItem> item;

    protected UltraSrtNcstItems(Parcel in)
    {
        item = in.createTypedArrayList(UltraSrtNcstItem.CREATOR);
    }

    public static final Creator<UltraSrtNcstItems> CREATOR = new Creator<UltraSrtNcstItems>()
    {
        @Override
        public UltraSrtNcstItems createFromParcel(Parcel in)
        {
            return new UltraSrtNcstItems(in);
        }

        @Override
        public UltraSrtNcstItems[] newArray(int size)
        {
            return new UltraSrtNcstItems[size];
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

    public void setItem(List<UltraSrtNcstItem> item)
    {
        this.item = item;
    }

    public List<UltraSrtNcstItem> getItem()
    {
        return item;
    }
}
