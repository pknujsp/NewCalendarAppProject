package com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NearbyMsrstnListResponse implements Parcelable
{
    @Expose
    @SerializedName("body")
    private NearbyMsrstnListBody body;

    protected NearbyMsrstnListResponse(Parcel in)
    {
        body = in.readParcelable(NearbyMsrstnListBody.class.getClassLoader());
    }

    public static final Creator<NearbyMsrstnListResponse> CREATOR = new Creator<NearbyMsrstnListResponse>()
    {
        @Override
        public NearbyMsrstnListResponse createFromParcel(Parcel in)
        {
            return new NearbyMsrstnListResponse(in);
        }

        @Override
        public NearbyMsrstnListResponse[] newArray(int size)
        {
            return new NearbyMsrstnListResponse[size];
        }
    };

    public void setBody(NearbyMsrstnListBody body)
    {
        this.body = body;
    }

    public NearbyMsrstnListBody getBody()
    {
        return body;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeParcelable(body, i);
    }
}
