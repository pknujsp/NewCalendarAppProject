package com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.basicinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Address
{
    @SerializedName("newaddr")
    @Expose
    private NewAddress newAddress;

    @SerializedName("region")
    @Expose
    private Region region;

    @SerializedName("addrbunho")
    @Expose
    private String addrBunho;

    @SerializedName("addrdetail")
    @Expose
    private String addrDetail;

    public Address()
    {
    }

    public NewAddress getNewAddress()
    {
        return newAddress;
    }

    public void setNewAddress(NewAddress newAddress)
    {
        this.newAddress = newAddress;
    }

    public Region getRegion()
    {
        return region;
    }

    public void setRegion(Region region)
    {
        this.region = region;
    }

    public String getAddrBunho()
    {
        return addrBunho;
    }

    public void setAddrBunho(String addrBunho)
    {
        this.addrBunho = addrBunho;
    }

    public String getAddrDetail()
    {
        return addrDetail;
    }

    public void setAddrDetail(String addrDetail)
    {
        this.addrDetail = addrDetail;
    }
}
