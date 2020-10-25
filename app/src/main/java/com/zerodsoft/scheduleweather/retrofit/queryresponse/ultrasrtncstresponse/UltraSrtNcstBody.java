package com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UltraSrtNcstBody implements Parcelable
{
    @Expose
    @SerializedName("items")
    private List<UltraSrtNcstItem> items;


    protected UltraSrtNcstBody(Parcel in)
    {
        items = in.createTypedArrayList(UltraSrtNcstItem.CREATOR);
    }

    public static final Creator<UltraSrtNcstBody> CREATOR = new Creator<UltraSrtNcstBody>()
    {
        @Override
        public UltraSrtNcstBody createFromParcel(Parcel in)
        {
            return new UltraSrtNcstBody(in);
        }

        @Override
        public UltraSrtNcstBody[] newArray(int size)
        {
            return new UltraSrtNcstBody[size];
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

    public UltraSrtNcstBody setItems(List<UltraSrtNcstItem> items)
    {
        this.items = items;
        return this;
    }

    public List<UltraSrtNcstItem> getItems()
    {
        return items;
    }
}
