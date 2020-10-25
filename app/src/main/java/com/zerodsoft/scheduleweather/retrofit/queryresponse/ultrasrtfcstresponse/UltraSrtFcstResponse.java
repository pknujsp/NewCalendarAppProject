package com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UltraSrtFcstResponse implements Parcelable
{
    @Expose
    @SerializedName("body")
    private UltraSrtFcstBody body;

    protected UltraSrtFcstResponse(Parcel in)
    {
        body = in.readParcelable(UltraSrtFcstBody.class.getClassLoader());
    }

    public static final Creator<UltraSrtFcstResponse> CREATOR = new Creator<UltraSrtFcstResponse>()
    {
        @Override
        public UltraSrtFcstResponse createFromParcel(Parcel in)
        {
            return new UltraSrtFcstResponse(in);
        }

        @Override
        public UltraSrtFcstResponse[] newArray(int size)
        {
            return new UltraSrtFcstResponse[size];
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

    public UltraSrtFcstResponse setBody(UltraSrtFcstBody body)
    {
        this.body = body;
        return this;
    }

    public UltraSrtFcstBody getBody()
    {
        return body;
    }
}
