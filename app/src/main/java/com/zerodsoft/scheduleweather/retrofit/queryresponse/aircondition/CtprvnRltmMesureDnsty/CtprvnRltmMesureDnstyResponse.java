package com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.CtprvnRltmMesureDnsty;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CtprvnRltmMesureDnstyResponse implements Parcelable
{
    @Expose
    @SerializedName("body")
    private CtprvnRltmMesureDnstyBody body;

    protected CtprvnRltmMesureDnstyResponse(Parcel in)
    {
        body = in.readParcelable(CtprvnRltmMesureDnstyBody.class.getClassLoader());
    }

    public static final Creator<CtprvnRltmMesureDnstyResponse> CREATOR = new Creator<CtprvnRltmMesureDnstyResponse>()
    {
        @Override
        public CtprvnRltmMesureDnstyResponse createFromParcel(Parcel in)
        {
            return new CtprvnRltmMesureDnstyResponse(in);
        }

        @Override
        public CtprvnRltmMesureDnstyResponse[] newArray(int size)
        {
            return new CtprvnRltmMesureDnstyResponse[size];
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

    public void setBody(CtprvnRltmMesureDnstyBody body)
    {
        this.body = body;
    }

    public CtprvnRltmMesureDnstyBody getBody()
    {
        return body;
    }
}
