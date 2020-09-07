package com.zerodsoft.scheduleweather.retrofit.queryresponse.coordtoaddressresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategoryDocuments;

import java.util.List;

public class CoordToAddress implements Parcelable
{
    @SerializedName("meta")
    @Expose
    private CoordToAddressMeta coordToAddressMeta;

    @SerializedName("documents")
    @Expose
    List<CoordToAddressDocuments> coordToAddressDocuments;

    protected CoordToAddress(Parcel in)
    {
        coordToAddressMeta = in.readParcelable(CoordToAddressMeta.class.getClassLoader());
        coordToAddressDocuments = in.createTypedArrayList(CoordToAddressDocuments.CREATOR);
    }

    public static final Creator<CoordToAddress> CREATOR = new Creator<CoordToAddress>()
    {
        @Override
        public CoordToAddress createFromParcel(Parcel in)
        {
            return new CoordToAddress(in);
        }

        @Override
        public CoordToAddress[] newArray(int size)
        {
            return new CoordToAddress[size];
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
        parcel.writeParcelable(coordToAddressMeta, i);
        parcel.writeTypedList(coordToAddressDocuments);
    }

    public CoordToAddressMeta getCoordToAddressMeta()
    {
        return coordToAddressMeta;
    }

    public void setCoordToAddressMeta(CoordToAddressMeta coordToAddressMeta)
    {
        this.coordToAddressMeta = coordToAddressMeta;
    }

    public List<CoordToAddressDocuments> getCoordToAddressDocuments()
    {
        return coordToAddressDocuments;
    }

    public void setCoordToAddressDocuments(List<CoordToAddressDocuments> coordToAddressDocuments)
    {
        this.coordToAddressDocuments = coordToAddressDocuments;
    }
}
