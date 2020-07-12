package com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddressResponseMeta implements Serializable
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
