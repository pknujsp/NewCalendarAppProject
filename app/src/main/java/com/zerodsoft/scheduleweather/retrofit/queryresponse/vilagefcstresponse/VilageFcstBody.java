package com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VilageFcstBody implements Parcelable
{
    @Expose
    @SerializedName("items")
    private List<VilageFcstItem> items;

    protected VilageFcstBody(Parcel in)
    {
        items = in.createTypedArrayList(VilageFcstItem.CREATOR);
    }

    public static final Creator<VilageFcstBody> CREATOR = new Creator<VilageFcstBody>()
    {
        @Override
        public VilageFcstBody createFromParcel(Parcel in)
        {
            return new VilageFcstBody(in);
        }

        @Override
        public VilageFcstBody[] newArray(int size)
        {
            return new VilageFcstBody[size];
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

    public VilageFcstBody setItems(List<VilageFcstItem> items)
    {
        this.items = items;
        return this;
    }

    public List<VilageFcstItem> getItems()
    {
        return items;
    }
}
