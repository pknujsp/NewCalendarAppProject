package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.midtaresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MidTaResponse implements Parcelable
{
    @Expose
    @SerializedName("body")
    private MidTaBody body;


    protected MidTaResponse(Parcel in)
    {
        body = in.readParcelable(MidTaBody.class.getClassLoader());
    }

    public static final Creator<MidTaResponse> CREATOR = new Creator<MidTaResponse>()
    {
        @Override
        public MidTaResponse createFromParcel(Parcel in)
        {
            return new MidTaResponse(in);
        }

        @Override
        public MidTaResponse[] newArray(int size)
        {
            return new MidTaResponse[size];
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
        parcel.writeParcelable(body, i);
    }

    public void setBody(MidTaBody body)
    {
        this.body = body;
    }

    public MidTaBody getBody()
    {
        return body;
    }
}
