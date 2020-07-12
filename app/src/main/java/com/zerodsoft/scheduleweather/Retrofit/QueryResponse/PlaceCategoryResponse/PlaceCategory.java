package com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceCategory
{
    @SerializedName("meta")
    @Expose
    private PlaceCategoryMeta placeCategoryMeta;

    @SerializedName("documents")
    @Expose
    private List<PlaceCategoryDocuments> placeCategoryDocuments;

    public PlaceCategoryMeta getPlaceCategoryMeta()
    {
        return placeCategoryMeta;
    }

    public void setPlaceCategoryMeta(PlaceCategoryMeta placeCategoryMeta)
    {
        this.placeCategoryMeta = placeCategoryMeta;
    }

    public List<PlaceCategoryDocuments> getPlaceCategoryDocuments()
    {
        return placeCategoryDocuments;
    }

    public void setPlaceCategoryDocuments(List<PlaceCategoryDocuments> placeCategoryDocuments)
    {
        this.placeCategoryDocuments = placeCategoryDocuments;
    }
}
