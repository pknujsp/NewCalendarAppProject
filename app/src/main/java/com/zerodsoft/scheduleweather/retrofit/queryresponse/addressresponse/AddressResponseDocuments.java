package com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressResponseDocuments implements Parcelable
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

    protected AddressResponseDocuments(Parcel in)
    {
        addressName = in.readString();
        addressType = in.readString();
        x = in.readDouble();
        y = in.readDouble();
        addressResponseAddress = in.readParcelable(AddressResponseAddress.class.getClassLoader());
        addressResponseRoadAddress = in.readParcelable(AddressResponseRoadAddress.class.getClassLoader());
    }

    public static final Creator<AddressResponseDocuments> CREATOR = new Creator<AddressResponseDocuments>()
    {
        @Override
        public AddressResponseDocuments createFromParcel(Parcel in)
        {
            return new AddressResponseDocuments(in);
        }

        @Override
        public AddressResponseDocuments[] newArray(int size)
        {
            return new AddressResponseDocuments[size];
        }
    };

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

    public String getAddressTypeStr()
    {
        if (addressType.equals(ROAD) || addressType.equals(ROAD_ADDR))
        {
            return "도로명";
        } else
        {
            return "지번";
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(this.addressName);
        parcel.writeString(this.addressType);
        parcel.writeDouble(this.x);
        parcel.writeDouble(this.y);
        parcel.writeParcelable(this.addressResponseAddress, i);
        parcel.writeParcelable(this.addressResponseRoadAddress, i);
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
