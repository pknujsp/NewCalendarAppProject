package com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceCategoryMeta implements Parcelable
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
    private PlaceCategorySameName placeCategorySameName;

    protected PlaceCategoryMeta(Parcel in)
    {
        totalCount = in.readInt();
        pageableCount = in.readInt();
        isEnd = in.readByte() != 0;
        placeCategorySameName = in.readParcelable(PlaceCategorySameName.class.getClassLoader());
    }

    public static final Creator<PlaceCategoryMeta> CREATOR = new Creator<PlaceCategoryMeta>()
    {
        @Override
        public PlaceCategoryMeta createFromParcel(Parcel in)
        {
            return new PlaceCategoryMeta(in);
        }

        @Override
        public PlaceCategoryMeta[] newArray(int size)
        {
            return new PlaceCategoryMeta[size];
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

    public PlaceCategorySameName getPlaceCategorySameName()
    {
        return placeCategorySameName;
    }

    public void setPlaceCategorySameName(PlaceCategorySameName placeCategorySameName)
    {
        this.placeCategorySameName = placeCategorySameName;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(totalCount);
        parcel.writeInt(pageableCount);
        parcel.writeByte((byte) (isEnd ? 1 : 0));
        parcel.writeParcelable(placeCategorySameName, i);
    }

}
