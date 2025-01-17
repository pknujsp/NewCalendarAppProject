package com.zerodsoft.calendarplatform.retrofit.queryresponse.map.addressresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressResponseAddress implements Parcelable
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

    @SerializedName("region_3depth_h_name")
    @Expose
    private String region3DepthHName;

    @SerializedName("h_code")
    @Expose
    private String hCode;

    @SerializedName("b_code")
    @Expose
    private String bCode;

    @SerializedName("mountain_yn")
    @Expose
    private String mountainYn;

    @SerializedName("main_address_no")
    @Expose
    private String mainAddressNo;

    @SerializedName("sub_address_no")
    @Expose
    private String subAddressNo;

    @SerializedName("zip_code")
    @Expose
    private String zipCode;

    @SerializedName("x")
    @Expose
    private double x;

    @SerializedName("y")
    @Expose
    private double y;

    public AddressResponseAddress()
    {
    }

    protected AddressResponseAddress(Parcel in)
    {
        addressName = in.readString();
        region1DepthName = in.readString();
        region2DepthName = in.readString();
        region3DepthName = in.readString();
        region3DepthHName = in.readString();
        hCode = in.readString();
        bCode = in.readString();
        mountainYn = in.readString();
        mainAddressNo = in.readString();
        subAddressNo = in.readString();
        zipCode = in.readString();
        x = in.readDouble();
        y = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(addressName);
        dest.writeString(region1DepthName);
        dest.writeString(region2DepthName);
        dest.writeString(region3DepthName);
        dest.writeString(region3DepthHName);
        dest.writeString(hCode);
        dest.writeString(bCode);
        dest.writeString(mountainYn);
        dest.writeString(mainAddressNo);
        dest.writeString(subAddressNo);
        dest.writeString(zipCode);
        dest.writeDouble(x);
        dest.writeDouble(y);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<AddressResponseAddress> CREATOR = new Creator<AddressResponseAddress>()
    {
        @Override
        public AddressResponseAddress createFromParcel(Parcel in)
        {
            return new AddressResponseAddress(in);
        }

        @Override
        public AddressResponseAddress[] newArray(int size)
        {
            return new AddressResponseAddress[size];
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

    public String getRegion3DepthHName()
    {
        return region3DepthHName;
    }

    public void setRegion3DepthHName(String region3DepthHName)
    {
        this.region3DepthHName = region3DepthHName;
    }

    public String gethCode()
    {
        return hCode;
    }

    public void sethCode(String hCode)
    {
        this.hCode = hCode;
    }

    public String getbCode()
    {
        return bCode;
    }

    public void setbCode(String bCode)
    {
        this.bCode = bCode;
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

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
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
}
