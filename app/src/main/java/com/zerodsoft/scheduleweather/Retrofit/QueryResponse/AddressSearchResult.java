package com.zerodsoft.scheduleweather.Retrofit.QueryResponse;

import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordDocuments;

import java.util.List;

public class AddressSearchResult
{
    private List<AddressResponseDocuments> addressResponseDocuments;
    private List<PlaceKeywordDocuments> placeKeywordDocuments;
    private List<PlaceCategoryDocuments> placeCategoryDocuments;
    private int resultNum;

    public AddressSearchResult()
    {
        resultNum = 0;
    }

    public AddressSearchResult setAddressResponseDocuments(List<AddressResponseDocuments> addressResponseDocuments)
    {
        this.addressResponseDocuments = addressResponseDocuments;
        resultNum++;
        return this;
    }

    public AddressSearchResult setPlaceKeywordDocuments(List<PlaceKeywordDocuments> placeKeywordDocuments)
    {
        this.placeKeywordDocuments = placeKeywordDocuments;
        resultNum++;
        return this;
    }

    public AddressSearchResult setPlaceCategoryDocuments(List<PlaceCategoryDocuments> placeCategoryDocuments)
    {
        this.placeCategoryDocuments = placeCategoryDocuments;
        resultNum++;
        return this;
    }

    public int getResultNum()
    {
        return resultNum;
    }

    public List<AddressResponseDocuments> getAddressResponseDocuments()
    {
        return addressResponseDocuments;
    }

    public List<PlaceCategoryDocuments> getPlaceCategoryDocuments()
    {
        return placeCategoryDocuments;
    }

    public List<PlaceKeywordDocuments> getPlaceKeywordDocuments()
    {
        return placeKeywordDocuments;
    }
}
