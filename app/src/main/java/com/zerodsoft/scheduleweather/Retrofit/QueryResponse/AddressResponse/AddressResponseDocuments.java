package com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressResponseDocuments
{
    @SerializedName("address_name")
    @Expose
    private String addressName;

    @SerializedName("address_type")
    @Expose
    private String addressType;

    @SerializedName("x")
    @Expose
    private double x;

    @SerializedName("y")
    @Expose
    private double y;

    @SerializedName("address")
    @Expose
    private AddressResponseAddress addressResponseAddress;

    @SerializedName("road_address")
    @Expose
    private AddressResponseRoadAddress addressResponseRoadAddress;

    public static final String REGION = "REGION";
    public static final String ROAD = "ROAD";
    public static final String REGION_ADDR = "REGION_ADDR";
    public static final String ROAD_ADDR = "ROAD_ADDR";

    public String getAddressName()
    {
        return addressName;
    }

    public void setAddressName(String addressName)
    {
        this.addressName = addressName;
    }

    public String getAddressType()
    {
        return addressType;
    }

    public void setAddressType(String addressType)
    {
        this.addressType = addressType;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public AddressResponseAddress getAddressResponseAddress()
    {
        return addressResponseAddress;
    }

    public void setAddressResponseAddress(AddressResponseAddress addressResponseAddress)
    {
        this.addressResponseAddress = addressResponseAddress;
    }

    public AddressResponseRoadAddress getAddressResponseRoadAddress()
    {
        return addressResponseRoadAddress;
    }

    public void setAddressResponseRoadAddress(AddressResponseRoadAddress addressResponseRoadAddress)
    {
        this.addressResponseRoadAddress = addressResponseRoadAddress;
    }
}
