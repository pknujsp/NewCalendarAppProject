package com.zerodsoft.scheduleweather.retrofit.queryresponse.ultrasrtncstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaResponse;

public class UltraSrtNcstRoot implements Parcelable
{
    @Expose
    @SerializedName("response")
    private UltraSrtNcstResponse response;

    protected UltraSrtNcstRoot(Parcel in)
    {
        response = in.readParcelable(UltraSrtNcstResponse.class.getClassLoader());
    }

    public static final Creator<UltraSrtNcstRoot> CREATOR = new Creator<UltraSrtNcstRoot>()
    {
        @Override
        public UltraSrtNcstRoot createFromParcel(Parcel in)
        {
            return new UltraSrtNcstRoot(in);
        }

        @Override
        public UltraSrtNcstRoot[] newArray(int size)
        {
            return new UltraSrtNcstRoot[size];
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

    public UltraSrtNcstRoot setResponse(UltraSrtNcstResponse response)
    {
        this.response = response;
        return this;
    }

    public UltraSrtNcstResponse getResponse()
    {
        return response;
    }
}
