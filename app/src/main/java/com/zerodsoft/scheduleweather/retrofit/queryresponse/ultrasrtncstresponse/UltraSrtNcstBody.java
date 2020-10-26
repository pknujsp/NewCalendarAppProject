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
    private UltraSrtNcstItems items;

    protected UltraSrtNcstBody(Parcel in)
    {
        items = in.readParcelable(UltraSrtNcstItems.class.getClassLoader());
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
        parcel.writeParcelable(items, i);
    }

    public void setItems(UltraSrtNcstItems items)
    {
        this.items = items;
    }

    public UltraSrtNcstItems getItems()
    {
        return items;
    }
}
