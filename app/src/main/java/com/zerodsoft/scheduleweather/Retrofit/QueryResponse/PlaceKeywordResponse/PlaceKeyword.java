package com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceKeyword
{
    @SerializedName("meta")
    @Expose
    private PlaceKeywordMeta placeKeywordMeta;

    @SerializedName("documents")
    @Expose
    private List<PlaceKeywordDocuments> placeKeywordDocuments;

    public PlaceKeywordMeta getPlaceKeywordMeta()
    {
        return placeKeywordMeta;
    }

    public void setPlaceKeywordMeta(PlaceKeywordMeta placeKeywordMeta)
    {
        this.placeKeywordMeta = placeKeywordMeta;
    }

    public List<PlaceKeywordDocuments> getPlaceKeywordDocuments()
    {
        return placeKeywordDocuments;
    }

    public void setPlaceKeywordDocuments(List<PlaceKeywordDocuments> placeKeywordDocuments)
    {
        this.placeKeywordDocuments = placeKeywordDocuments;
    }
}
