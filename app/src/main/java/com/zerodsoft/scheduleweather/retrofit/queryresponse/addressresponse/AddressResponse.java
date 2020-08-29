package com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddressResponse implements Parcelable
{
    @SerializedName("meta")
    @Expose
    private AddressResponseMeta addressResponseMeta;

    @SerializedName("documents")
    @Expose
    private List<AddressResponseDocuments> addressResponseDocumentsList;

    protected AddressResponse(Parcel in)
    {
        addressResponseMeta = in.readParcelable(AddressResponseMeta.class.getClassLoader());
        addressResponseDocumentsList = in.createTypedArrayList(AddressResponseDocuments.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(addressResponseMeta, flags);
        dest.writeTypedList(addressResponseDocumentsList);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<AddressResponse> CREATOR = new Creator<AddressResponse>()
    {
        @Override
        public AddressResponse createFromParcel(Parcel in)
        {
            return new AddressResponse(in);
        }

        @Override
        public AddressResponse[] newArray(int size)
        {
            return new AddressResponse[size];
        }
    };

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
