package com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MidTaBody implements Parcelable
{
    @Expose
    @SerializedName("items")
    private List<MidTaItem> items;

    protected MidTaBody(Parcel in)
    {
        items = in.createTypedArrayList(MidTaItem.CREATOR);
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
        parcel.writeTypedList(items);
    }

    public MidTaBody setItems(List<MidTaItem> items)
    {
        this.items = items;
        return this;
    }

    public List<MidTaItem> getItems()
    {
        return items;
    }
}
