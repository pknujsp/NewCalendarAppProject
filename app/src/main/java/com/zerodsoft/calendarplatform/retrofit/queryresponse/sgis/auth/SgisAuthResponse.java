package com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.auth;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SgisAuthResponse implements Parcelable
{
    @Expose
    @SerializedName("result")
    private SgisAuthResult result;

    protected SgisAuthResponse(Parcel in)
    {
        result = in.readParcelable(SgisAuthResult.class.getClassLoader());
    }

    public static final Creator<SgisAuthResponse> CREATOR = new Creator<SgisAuthResponse>()
    {
        @Override
        public SgisAuthResponse createFromParcel(Parcel in)
        {
            return new SgisAuthResponse(in);
        }

        @Override
        public SgisAuthResponse[] newArray(int size)
        {
            return new SgisAuthResponse[size];
        }
    };

    public void setResult(SgisAuthResult result)
    {
        this.result = result;
    }

    public SgisAuthResult getResult()
    {
        return result;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeParcelable(result, i);
    }
}
