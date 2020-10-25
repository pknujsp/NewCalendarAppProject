package com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UltraSrtFcstBody implements Parcelable
{
    @Expose
    @SerializedName("items")
    private List<UltraSrtFcstItem> items;

    protected UltraSrtFcstBody(Parcel in)
    {
        items = in.createTypedArrayList(UltraSrtFcstItem.CREATOR);
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
        parcel.writeTypedList(items);
    }

    public UltraSrtFcstBody setItems(List<UltraSrtFcstItem> items)
    {
        this.items = items;
        return this;
    }

    public List<UltraSrtFcstItem> getItems()
    {
        return items;
    }
}
