package com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalResponse;

import java.util.List;

public class AddressKakaoLocalResponse extends KakaoLocalResponse implements Parcelable
{
    @SerializedName("meta")
    @Expose
    private AddressResponseMeta addressResponseMeta;

    @SerializedName("documents")
    @Expose
    private List<AddressResponseDocuments> addressResponseDocumentsList;

    protected AddressKakaoLocalResponse(Parcel in)
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

    public static final Creator<AddressKakaoLocalResponse> CREATOR = new Creator<AddressKakaoLocalResponse>()
    {
        @Override
        public AddressKakaoLocalResponse createFromParcel(Parcel in)
        {
            return new AddressKakaoLocalResponse(in);
        }

        @Override
        public AddressKakaoLocalResponse[] newArray(int size)
        {
            return new AddressKakaoLocalResponse[size];
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
