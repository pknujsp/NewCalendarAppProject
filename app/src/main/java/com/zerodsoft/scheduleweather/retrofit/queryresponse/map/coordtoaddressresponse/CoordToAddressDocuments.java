package com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;

public class CoordToAddressDocuments extends KakaoLocalDocument implements Parcelable
{
    @SerializedName("address")
    @Expose
    private CoordToAddressAddress coordToAddressAddress;

    @SerializedName("road_address")
    private CoordToAddressRoadAddress coordToAddressRoadAddress;


    protected CoordToAddressDocuments(Parcel in)
    {
        coordToAddressAddress = in.readParcelable(CoordToAddressAddress.class.getClassLoader());
        coordToAddressRoadAddress = in.readParcelable(CoordToAddressRoadAddress.class.getClassLoader());
    }

    public static final Creator<CoordToAddressDocuments> CREATOR = new Creator<CoordToAddressDocuments>()
    {
        @Override
        public CoordToAddressDocuments createFromParcel(Parcel in)
        {
            return new CoordToAddressDocuments(in);
        }

        @Override
        public CoordToAddressDocuments[] newArray(int size)
        {
            return new CoordToAddressDocuments[size];
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
        parcel.writeParcelable(coordToAddressAddress, i);
        parcel.writeParcelable(coordToAddressRoadAddress, i);
    }

    public CoordToAddressAddress getCoordToAddressAddress()
    {
        return coordToAddressAddress;
    }

    public void setCoordToAddressAddress(CoordToAddressAddress coordToAddressAddress)
    {
        this.coordToAddressAddress = coordToAddressAddress;
    }

    public CoordToAddressRoadAddress getCoordToAddressRoadAddress()
    {
        return coordToAddressRoadAddress;
    }

    public void setCoordToAddressRoadAddress(CoordToAddressRoadAddress coordToAddressRoadAddress)
    {
        this.coordToAddressRoadAddress = coordToAddressRoadAddress;
    }
}
