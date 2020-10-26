package com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MidLandFcstItems implements Parcelable
{
    @Expose
    @SerializedName("item")
    private List<MidLandFcstItem> item;

    protected MidLandFcstItems(Parcel in)
    {
        item = in.createTypedArrayList(MidLandFcstItem.CREATOR);
    }

    public static final Creator<MidLandFcstItems> CREATOR = new Creator<MidLandFcstItems>()
    {
        @Override
        public MidLandFcstItems createFromParcel(Parcel in)
        {
            return new MidLandFcstItems(in);
        }

        @Override
        public MidLandFcstItems[] newArray(int size)
        {
            return new MidLandFcstItems[size];
        }
    };

    public List<MidLandFcstItem> getItem()
    {
        return item;
    }

    public void setItem(List<MidLandFcstItem> item)
    {
        this.item = item;
    }

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
}
