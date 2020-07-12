package com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceKeywordSameName
{
    @SerializedName("region")
    @Expose
    private String[] region;

    @SerializedName("keyword")
    @Expose
    private String keyword;

    @SerializedName("selected_region")
    @Expose
    private String selectedRegion;

    public String[] getRegion()
    {
        return region;
    }

    public void setRegion(String[] region)
    {
        this.region = region;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public String getSelectedRegion()
    {
        return selectedRegion;
    }

    public void setSelectedRegion(String selectedRegion)
    {
        this.selectedRegion = selectedRegion;
    }
}
