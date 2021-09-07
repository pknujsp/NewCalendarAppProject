package com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.NearbyMsrstnList;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.aircondition.FindStationRoot;

public class NearbyMsrstnListRoot extends FindStationRoot implements Parcelable
{
    @Expose
    @SerializedName("response")
    private NearbyMsrstnListResponse response;


    protected NearbyMsrstnListRoot(Parcel in)
    {
        response = in.readParcelable(NearbyMsrstnListResponse.class.getClassLoader());
    }

    public static final Creator<NearbyMsrstnListRoot> CREATOR = new Creator<NearbyMsrstnListRoot>()
    {
        @Override
        public NearbyMsrstnListRoot createFromParcel(Parcel in)
        {
            return new NearbyMsrstnListRoot(in);
        }

        @Override
        public NearbyMsrstnListRoot[] newArray(int size)
        {
            return new NearbyMsrstnListRoot[size];
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
        parcel.writeParcelable(response, i);
    }

    public void setResponse(NearbyMsrstnListResponse response)
    {
        this.response = response;
    }

    public NearbyMsrstnListResponse getResponse()
    {
        return response;
    }
}
