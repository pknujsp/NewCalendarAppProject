package com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.address.reversegeocoding;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReverseGeoCodingResponse implements Parcelable
{
    @Expose
    @SerializedName("result")
    private List<ReverseGeoCodingItem> result;


    protected ReverseGeoCodingResponse(Parcel in)
    {
        result = in.createTypedArrayList(ReverseGeoCodingItem.CREATOR);
    }

    public static final Creator<ReverseGeoCodingResponse> CREATOR = new Creator<ReverseGeoCodingResponse>()
    {
        @Override
        public ReverseGeoCodingResponse createFromParcel(Parcel in)
        {
            return new ReverseGeoCodingResponse(in);
        }

        @Override
        public ReverseGeoCodingResponse[] newArray(int size)
        {
            return new ReverseGeoCodingResponse[size];
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
        parcel.writeTypedList(result);
    }
}
