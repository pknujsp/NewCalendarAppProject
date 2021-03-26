package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.ultrasrtncstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UltraSrtNcstResponse implements Parcelable
{
    @Expose
    @SerializedName("body")
    private UltraSrtNcstBody body;

    protected UltraSrtNcstResponse(Parcel in)
    {
        body = in.readParcelable(UltraSrtNcstBody.class.getClassLoader());
    }

    public static final Creator<UltraSrtNcstResponse> CREATOR = new Creator<UltraSrtNcstResponse>()
    {
        @Override
        public UltraSrtNcstResponse createFromParcel(Parcel in)
        {
            return new UltraSrtNcstResponse(in);
        }

        @Override
        public UltraSrtNcstResponse[] newArray(int size)
        {
            return new UltraSrtNcstResponse[size];
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

    public void setBody(UltraSrtNcstBody body)
    {
        this.body = body;
    }

    public UltraSrtNcstBody getBody()
    {
        return body;
    }
}
