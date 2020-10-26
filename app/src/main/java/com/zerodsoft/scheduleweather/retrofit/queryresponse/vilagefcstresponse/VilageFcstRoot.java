package com.zerodsoft.scheduleweather.retrofit.queryresponse.vilagefcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.midtaresponse.MidTaResponse;

public class VilageFcstRoot implements Parcelable
{
    @Expose
    @SerializedName("response")
    private VilageFcstResponse response;

    protected VilageFcstRoot(Parcel in)
    {
        response = in.readParcelable(VilageFcstResponse.class.getClassLoader());
    }

    public static final Creator<VilageFcstRoot> CREATOR = new Creator<VilageFcstRoot>()
    {
        @Override
        public VilageFcstRoot createFromParcel(Parcel in)
        {
            return new VilageFcstRoot(in);
        }

        @Override
        public VilageFcstRoot[] newArray(int size)
        {
            return new VilageFcstRoot[size];
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

    public void setResponse(VilageFcstResponse response)
    {
        this.response = response;
    }

    public VilageFcstResponse getResponse()
    {
        return response;
    }
}
