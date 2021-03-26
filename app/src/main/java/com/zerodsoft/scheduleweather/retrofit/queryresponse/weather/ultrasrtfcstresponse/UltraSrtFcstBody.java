package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UltraSrtFcstBody implements Parcelable
{
    @Expose
    @SerializedName("items")
    private UltraSrtFcstItems items;

    protected UltraSrtFcstBody(Parcel in)
    {
        items = in.readParcelable(UltraSrtFcstItems.class.getClassLoader());
    }

    public static final Creator<UltraSrtFcstBody> CREATOR = new Creator<UltraSrtFcstBody>()
    {
        @Override
        public UltraSrtFcstBody createFromParcel(Parcel in)
        {
            return new UltraSrtFcstBody(in);
        }

        @Override
        public UltraSrtFcstBody[] newArray(int size)
        {
            return new UltraSrtFcstBody[size];
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
        parcel.writeParcelable(items, i);
    }

    public void setItems(UltraSrtFcstItems items)
    {
        this.items = items;
    }

    public UltraSrtFcstItems getItems()
    {
        return items;
    }
}
