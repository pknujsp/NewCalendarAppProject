package com.zerodsoft.scheduleweather.retrofit.queryresponse.midlandfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MidLandFcstResponse implements Parcelable
{
    @Expose
    @SerializedName("body")
    private MidLandFcstBody body;


    protected MidLandFcstResponse(Parcel in)
    {
        body = in.readParcelable(MidLandFcstBody.class.getClassLoader());
    }

    public static final Creator<MidLandFcstResponse> CREATOR = new Creator<MidLandFcstResponse>()
    {
        @Override
        public MidLandFcstResponse createFromParcel(Parcel in)
        {
            return new MidLandFcstResponse(in);
        }

        @Override
        public MidLandFcstResponse[] newArray(int size)
        {
            return new MidLandFcstResponse[size];
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

    public void setBody(MidLandFcstBody body)
    {
        this.body = body;
    }

    public MidLandFcstBody getBody()
    {
        return body;
    }
}
