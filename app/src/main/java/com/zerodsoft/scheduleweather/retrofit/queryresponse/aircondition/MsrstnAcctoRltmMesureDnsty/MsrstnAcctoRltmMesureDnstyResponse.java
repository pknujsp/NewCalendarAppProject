package com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MsrstnAcctoRltmMesureDnstyResponse implements Parcelable
{
    @Expose
    @SerializedName("body")
    private MsrstnAcctoRltmMesureDnstyBody body;


    protected MsrstnAcctoRltmMesureDnstyResponse(Parcel in)
    {
        body = in.readParcelable(MsrstnAcctoRltmMesureDnstyBody.class.getClassLoader());
    }

    public static final Creator<MsrstnAcctoRltmMesureDnstyResponse> CREATOR = new Creator<MsrstnAcctoRltmMesureDnstyResponse>()
    {
        @Override
        public MsrstnAcctoRltmMesureDnstyResponse createFromParcel(Parcel in)
        {
            return new MsrstnAcctoRltmMesureDnstyResponse(in);
        }

        @Override
        public MsrstnAcctoRltmMesureDnstyResponse[] newArray(int size)
        {
            return new MsrstnAcctoRltmMesureDnstyResponse[size];
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

    public MsrstnAcctoRltmMesureDnstyBody getBody()
    {
        return body;
    }

    public void setBody(MsrstnAcctoRltmMesureDnstyBody body)
    {
        this.body = body;
    }
}
