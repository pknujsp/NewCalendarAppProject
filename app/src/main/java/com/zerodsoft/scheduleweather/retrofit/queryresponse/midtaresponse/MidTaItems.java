package com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MidTaItems implements Parcelable
{
    @Expose
    @SerializedName("item")
    private List<MidTaItem> item;

    protected MidTaItems(Parcel in)
    {
        item = in.createTypedArrayList(MidTaItem.CREATOR);
    }

    public static final Creator<MidTaItems> CREATOR = new Creator<MidTaItems>()
    {
        @Override
        public MidTaItems createFromParcel(Parcel in)
        {
            return new MidTaItems(in);
        }

        @Override
        public MidTaItems[] newArray(int size)
        {
            return new MidTaItems[size];
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

    public void setItem(List<MidTaItem> item)
    {
        this.item = item;
    }

    public List<MidTaItem> getItem()
    {
        return item;
    }
}
