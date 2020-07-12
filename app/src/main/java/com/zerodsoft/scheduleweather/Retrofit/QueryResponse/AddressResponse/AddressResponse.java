package com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddressResponse
{
    @SerializedName("meta")
    @Expose
    private AddressResponseMeta addressResponseMeta;

    @SerializedName("documents")
    @Expose
    private List<AddressResponseDocuments> addressResponseDocumentsList;

    public AddressResponseMeta getAddressResponseMeta()
    {
        return addressResponseMeta;
    }

    public void setAddressResponseMeta(AddressResponseMeta addressResponseMeta)
    {
        this.addressResponseMeta = addressResponseMeta;
    }

    public List<AddressResponseDocuments> getAddressResponseDocumentsList()
    {
        return addressResponseDocumentsList;
    }

    public void setAddressResponseDocumentsList(List<AddressResponseDocuments> addressResponseDocumentsList)
    {
        this.addressResponseDocumentsList = addressResponseDocumentsList;
    }
}
