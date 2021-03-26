package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midtaresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MidTaBody implements Parcelable
{
    @Expose
    @SerializedName("items")
    private MidTaItems items;

    protected MidTaBody(Parcel in)
    {
        items = in.readParcelable(MidTaItems.class.getClassLoader());
    }

    public static final Creator<MidTaBody> CREATOR = new Creator<MidTaBody>()
    {
        @Override
        public MidTaBody createFromParcel(Parcel in)
        {
            return new MidTaBody(in);
        }

        @Override
        public MidTaBody[] newArray(int size)
        {
            return new MidTaBody[size];
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

    public void setItems(MidTaItems items)
    {
        this.items = items;
    }

    public MidTaItems getItems()
    {
        return items;
    }
}
