package com.zerodsoft.scheduleweather.kakaomap.model;

import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse.PlaceDocuments;

import net.daum.mf.map.api.MapPOIItem;

public class CustomPoiItem extends MapPOIItem
{
    private AddressResponseDocuments addressDocument;
    private PlaceDocuments placeDocument;

    public CustomPoiItem()
    {
    }

    public AddressResponseDocuments getAddressDocument()
    {
        return addressDocument;
    }

    public void setAddressDocument(AddressResponseDocuments addressDocument)
    {
        this.addressDocument = addressDocument;
    }

    public PlaceDocuments getPlaceDocument()
    {
        return placeDocument;
    }

    public void setPlaceDocument(PlaceDocuments placeDocument)
    {
        this.placeDocument = placeDocument;
    }
}
