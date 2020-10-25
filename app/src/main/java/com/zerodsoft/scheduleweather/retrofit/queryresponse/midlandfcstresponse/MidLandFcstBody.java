package com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MidLandFcstBody implements Parcelable
{
    @Expose
    @SerializedName("items")
    private List<MidLandFcstItem> items;


    protected MidLandFcstBody(Parcel in)
    {
        items = in.createTypedArrayList(MidLandFcstItem.CREATOR);
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
        parcel.writeTypedList(items);
    }

    public MidLandFcstBody setItems(List<MidLandFcstItem> items)
    {
        this.items = items;
        return this;
    }

    public List<MidLandFcstItem> getItems()
    {
        return items;
    }
}
