package com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.midlandfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MidLandFcstBody implements Parcelable
{
    @Expose
    @SerializedName("items")
    private MidLandFcstItems items;

    protected MidLandFcstBody(Parcel in)
    {
        items = in.readParcelable(MidLandFcstItems.class.getClassLoader());
    }

    public static final Creator<MidLandFcstBody> CREATOR = new Creator<MidLandFcstBody>()
    {
        @Override
        public MidLandFcstBody createFromParcel(Parcel in)
        {
            return new MidLandFcstBody(in);
        }

        @Override
        public MidLandFcstBody[] newArray(int size)
        {
            return new MidLandFcstBody[size];
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

    public void setItems(MidLandFcstItems items)
    {
        this.items = items;
    }

    public MidLandFcstItems getItems()
    {
        return items;
    }
}
