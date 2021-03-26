package com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceMeta implements Parcelable
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

    @SerializedName("same_name")
    @Expose
    private PlaceSameName placeSameName;

    public PlaceMeta()
    {
    }

    protected PlaceMeta(Parcel in)
    {
        totalCount = in.readInt();
        pageableCount = in.readInt();
        isEnd = in.readByte() != 0;
        placeSameName = in.readParcelable(PlaceSameName.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(totalCount);
        dest.writeInt(pageableCount);
        dest.writeByte((byte) (isEnd ? 1 : 0));
        dest.writeParcelable(placeSameName, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<PlaceMeta> CREATOR = new Creator<PlaceMeta>()
    {
        @Override
        public PlaceMeta createFromParcel(Parcel in)
        {
            return new PlaceMeta(in);
        }

        @Override
        public PlaceMeta[] newArray(int size)
        {
            return new PlaceMeta[size];
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

    public PlaceSameName getPlaceSameName()
    {
        return placeSameName;
    }

    public void setPlaceSameName(PlaceSameName placeSameName)
    {
        this.placeSameName = placeSameName;
    }


}
