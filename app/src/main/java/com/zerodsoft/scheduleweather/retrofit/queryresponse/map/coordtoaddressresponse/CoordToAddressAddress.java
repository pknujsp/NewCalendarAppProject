package com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoordToAddressAddress implements Parcelable
{
    @SerializedName("address_name")
    @Expose
    private String addressName;

    @SerializedName("region_1depth_name")
    @Expose
    private String region1DepthName;

    @SerializedName("region_2depth_name")
    @Expose
    private String region2DepthName;

    @SerializedName("region_3depth_name")
    @Expose
    private String region3DepthName;

    @SerializedName("mountain_yn")
    @Expose
    private String mountainYn;

    @SerializedName("main_address_no")
    @Expose
    private String mainAddressNo;

    @SerializedName("sub_address_no")
    @Expose
    private String subAddressNo;

    protected CoordToAddressAddress(Parcel in)
    {
        addressName = in.readString();
        region1DepthName = in.readString();
        region2DepthName = in.readString();
        region3DepthName = in.readString();
        mountainYn = in.readString();
        mainAddressNo = in.readString();
        subAddressNo = in.readString();
    }

    public static final Creator<CoordToAddressAddress> CREATOR = new Creator<CoordToAddressAddress>()
    {
        @Override
        public CoordToAddressAddress createFromParcel(Parcel in)
        {
            return new CoordToAddressAddress(in);
        }

        @Override
        public CoordToAddressAddress[] newArray(int size)
        {
            return new CoordToAddressAddress[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(addressName);
        parcel.writeString(region1DepthName);
        parcel.writeString(region2DepthName);
        parcel.writeString(region3DepthName);
        parcel.writeString(mountainYn);
        parcel.writeString(mainAddressNo);
        parcel.writeString(subAddressNo);
    }

    public String getAddressName()
    {
        return addressName;
    }

    public void setAddressName(String addressName)
    {
        this.addressName = addressName;
    }

    public String getRegion1DepthName()
    {
        return region1DepthName;
    }

    public void setRegion1DepthName(String region1DepthName)
    {
        this.region1DepthName = region1DepthName;
    }

    public String getRegion2DepthName()
    {
        return region2DepthName;
    }

    public void setRegion2DepthName(String region2DepthName)
    {
        this.region2DepthName = region2DepthName;
    }

    public String getRegion3DepthName()
    {
        return region3DepthName;
    }

    public void setRegion3DepthName(String region3DepthName)
    {
        this.region3DepthName = region3DepthName;
    }

    public String getMountainYn()
    {
        return mountainYn;
    }

    public void setMountainYn(String mountainYn)
    {
        this.mountainYn = mountainYn;
    }

    public String getMainAddressNo()
    {
        return mainAddressNo;
    }

    public void setMainAddressNo(String mainAddressNo)
    {
        this.mainAddressNo = mainAddressNo;
    }

    public String getSubAddressNo()
    {
        return subAddressNo;
    }

    public void setSubAddressNo(String subAddressNo)
    {
        this.subAddressNo = subAddressNo;
    }
}
