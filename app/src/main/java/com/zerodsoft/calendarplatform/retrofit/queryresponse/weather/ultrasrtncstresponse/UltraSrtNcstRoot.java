package com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.ultrasrtncstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    public void setResponse(UltraSrtNcstResponse response)
    {
        this.response = response;
    }

    public UltraSrtNcstResponse getResponse()
    {
        return response;
    }
}
