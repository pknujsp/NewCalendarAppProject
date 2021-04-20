package com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.AirConditionRoot;

public class MsrstnAcctoRltmMesureDnstyRoot extends AirConditionRoot implements Parcelable
{
    @Expose
    @SerializedName("response")
    private MsrstnAcctoRltmMesureDnstyResponse response;

    protected MsrstnAcctoRltmMesureDnstyRoot(Parcel in)
    {
        response = in.readParcelable(MsrstnAcctoRltmMesureDnstyResponse.class.getClassLoader());
    }

    public static final Creator<MsrstnAcctoRltmMesureDnstyRoot> CREATOR = new Creator<MsrstnAcctoRltmMesureDnstyRoot>()
    {
        @Override
        public MsrstnAcctoRltmMesureDnstyRoot createFromParcel(Parcel in)
        {
            return new MsrstnAcctoRltmMesureDnstyRoot(in);
        }

        @Override
        public MsrstnAcctoRltmMesureDnstyRoot[] newArray(int size)
        {
            return new MsrstnAcctoRltmMesureDnstyRoot[size];
        }
    };

    public void setResponse(MsrstnAcctoRltmMesureDnstyResponse response)
    {
        this.response = response;
    }

    public MsrstnAcctoRltmMesureDnstyResponse getResponse()
    {
        return response;
    }

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
}
