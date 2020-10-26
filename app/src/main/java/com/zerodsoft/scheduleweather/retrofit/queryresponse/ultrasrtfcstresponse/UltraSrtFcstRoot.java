package com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaResponse;

public class UltraSrtFcstRoot implements Parcelable
{
    @Expose
    @SerializedName("response")
    private UltraSrtFcstResponse response;


    protected UltraSrtFcstRoot(Parcel in)
    {
        response = in.readParcelable(UltraSrtFcstResponse.class.getClassLoader());
    }

    public static final Creator<UltraSrtFcstRoot> CREATOR = new Creator<UltraSrtFcstRoot>()
    {
        @Override
        public UltraSrtFcstRoot createFromParcel(Parcel in)
        {
            return new UltraSrtFcstRoot(in);
        }

        @Override
        public UltraSrtFcstRoot[] newArray(int size)
        {
            return new UltraSrtFcstRoot[size];
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

    public void setResponse(UltraSrtFcstResponse response)
    {
        this.response = response;
    }

    public UltraSrtFcstResponse getResponse()
    {
        return response;
    }
}
