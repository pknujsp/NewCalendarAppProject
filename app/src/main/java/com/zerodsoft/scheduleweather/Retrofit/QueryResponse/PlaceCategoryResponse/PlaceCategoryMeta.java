package com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordSameName;

import java.io.Serializable;

public class PlaceCategoryMeta implements Serializable
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
}
