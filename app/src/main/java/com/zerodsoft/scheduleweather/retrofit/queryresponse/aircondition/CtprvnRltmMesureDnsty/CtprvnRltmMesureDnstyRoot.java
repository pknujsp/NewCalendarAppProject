package com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.CtprvnRltmMesureDnsty;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.AirConditionRoot;

public class CtprvnRltmMesureDnstyRoot extends AirConditionRoot implements Parcelable
{
    @Expose
    @SerializedName("response")
    private CtprvnRltmMesureDnstyResponse response;

    protected CtprvnRltmMesureDnstyRoot(Parcel in)
    {
        response = in.readParcelable(CtprvnRltmMesureDnstyResponse.class.getClassLoader());
    }

    public static final Creator<CtprvnRltmMesureDnstyRoot> CREATOR = new Creator<CtprvnRltmMesureDnstyRoot>()
    {
        @Override
        public CtprvnRltmMesureDnstyRoot createFromParcel(Parcel in)
        {
            return new CtprvnRltmMesureDnstyRoot(in);
        }

        @Override
        public CtprvnRltmMesureDnstyRoot[] newArray(int size)
        {
            return new CtprvnRltmMesureDnstyRoot[size];
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

    public void setResponse(CtprvnRltmMesureDnstyResponse response)
    {
        this.response = response;
    }

    public CtprvnRltmMesureDnstyResponse getResponse()
    {
        return response;
    }
}
