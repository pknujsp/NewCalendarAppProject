package com.zerodsoft.scheduleweather.retrofit.queryresponse.coordtoaddressresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoordToAddressMeta implements Parcelable
{
    @SerializedName("total_count")
    @Expose
    private int totalCount;

    protected CoordToAddressMeta(Parcel in)
    {
        totalCount = in.readInt();
    }

    public static final Creator<CoordToAddressMeta> CREATOR = new Creator<CoordToAddressMeta>()
    {
        @Override
        public CoordToAddressMeta createFromParcel(Parcel in)
        {
            return new CoordToAddressMeta(in);
        }

        @Override
        public CoordToAddressMeta[] newArray(int size)
        {
            return new CoordToAddressMeta[size];
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
        parcel.writeInt(totalCount);
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getTotalCount()
    {
        return totalCount;
    }
}
