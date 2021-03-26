package com.zerodsoft.scheduleweather.retrofit.queryresponse.weather.vilagefcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VilageFcstResponse implements Parcelable
{
    @Expose
    @SerializedName("body")
    private VilageFcstBody body;

    protected VilageFcstResponse(Parcel in)
    {
        body = in.readParcelable(VilageFcstBody.class.getClassLoader());
    }

    public static final Creator<VilageFcstResponse> CREATOR = new Creator<VilageFcstResponse>()
    {
        @Override
        public VilageFcstResponse createFromParcel(Parcel in)
        {
            return new VilageFcstResponse(in);
        }

        @Override
        public VilageFcstResponse[] newArray(int size)
        {
            return new VilageFcstResponse[size];
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

    public void setBody(VilageFcstBody body)
    {
        this.body = body;
    }

    public VilageFcstBody getBody()
    {
        return body;
    }
}
