package com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MidTaRoot implements Parcelable
{
    @Expose
    @SerializedName("response")
    private MidTaResponse response;

    protected MidTaRoot(Parcel in)
    {
        response = in.readParcelable(MidTaResponse.class.getClassLoader());
    }

    public static final Creator<MidTaRoot> CREATOR = new Creator<MidTaRoot>()
    {
        @Override
        public MidTaRoot createFromParcel(Parcel in)
        {
            return new MidTaRoot(in);
        }

        @Override
        public MidTaRoot[] newArray(int size)
        {
            return new MidTaRoot[size];
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

    public MidTaRoot setResponse(MidTaResponse response)
    {
        this.response = response;
        return this;
    }

    public MidTaResponse getResponse()
    {
        return response;
    }
}
