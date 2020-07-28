package com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PlaceKeywordMeta implements Parcelable
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
    private PlaceKeywordSameName placeKeywordSameName;

    protected PlaceKeywordMeta(Parcel in)
    {
        totalCount = in.readInt();
        pageableCount = in.readInt();
        isEnd = in.readByte() != 0;
        placeKeywordSameName = in.readParcelable(PlaceKeywordSameName.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(totalCount);
        dest.writeInt(pageableCount);
        dest.writeByte((byte) (isEnd ? 1 : 0));
        dest.writeParcelable(placeKeywordSameName, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<PlaceKeywordMeta> CREATOR = new Creator<PlaceKeywordMeta>()
    {
        @Override
        public PlaceKeywordMeta createFromParcel(Parcel in)
        {
            return new PlaceKeywordMeta(in);
        }

        @Override
        public PlaceKeywordMeta[] newArray(int size)
        {
            return new PlaceKeywordMeta[size];
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

    public PlaceKeywordSameName getPlaceKeywordSameName()
    {
        return placeKeywordSameName;
    }

    public void setPlaceKeywordSameName(PlaceKeywordSameName placeKeywordSameName)
    {
        this.placeKeywordSameName = placeKeywordSameName;
    }


}
