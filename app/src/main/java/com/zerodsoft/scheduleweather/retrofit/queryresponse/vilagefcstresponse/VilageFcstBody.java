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
    private VilageFcstItems items;


    protected VilageFcstBody(Parcel in)
    {
        items = in.readParcelable(VilageFcstItems.class.getClassLoader());
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
        parcel.writeParcelable(items, i);
    }

    public void setItems(VilageFcstItems items)
    {
        this.items = items;
    }

    public VilageFcstItems getItems()
    {
        return items;
    }
}
