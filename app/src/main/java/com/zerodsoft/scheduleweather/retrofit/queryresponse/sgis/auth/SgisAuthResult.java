package com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.auth;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.SgisRoot;

public class SgisAuthResult extends SgisRoot implements Parcelable
{
    @Expose
    @SerializedName("accessToken")
    private String accessToken;

    @Expose
    @SerializedName("accessTimeout")
    private String accessTimeout;

    protected SgisAuthResult(Parcel in)
    {
        accessToken = in.readString();
        accessTimeout = in.readString();
    }

    public static final Creator<SgisAuthResult> CREATOR = new Creator<SgisAuthResult>()
    {
        @Override
        public SgisAuthResult createFromParcel(Parcel in)
        {
            return new SgisAuthResult(in);
        }

        @Override
        public SgisAuthResult[] newArray(int size)
        {
            return new SgisAuthResult[size];
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
        parcel.writeString(accessToken);
        parcel.writeString(accessTimeout);
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public String getAccessTimeout()
    {
        return accessTimeout;
    }

    public void setAccessTimeout(String accessTimeout)
    {
        this.accessTimeout = accessTimeout;
    }
}
