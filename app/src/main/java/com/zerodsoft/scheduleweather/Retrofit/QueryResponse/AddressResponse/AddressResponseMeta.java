package com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddressResponseMeta implements Parcelable
{
    @SerializedName("total_count")
    @Expose
    private int totalCount;

    @SerializedName("pageable_count")
    @Expose
    private int pageableCount;

    @SerializedName("is_end")
    @Expose
    private boolean isEnd;

    protected AddressResponseMeta(Parcel in)
    {
        totalCount = in.readInt();
        pageableCount = in.readInt();
        isEnd = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(totalCount);
        dest.writeInt(pageableCount);
        dest.writeByte((byte) (isEnd ? 1 : 0));
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<AddressResponseMeta> CREATOR = new Creator<AddressResponseMeta>()
    {
        @Override
        public AddressResponseMeta createFromParcel(Parcel in)
        {
            return new AddressResponseMeta(in);
        }

        @Override
        public AddressResponseMeta[] newArray(int size)
        {
            return new AddressResponseMeta[size];
        }
    };

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getPageableCount()
    {
        return pageableCount;
    }

    public void setPageableCount(int pageableCount)
    {
        this.pageableCount = pageableCount;
    }

    public boolean isEnd()
    {
        return isEnd;
    }

    public void setEnd(boolean end)
    {
        isEnd = end;
    }
}
