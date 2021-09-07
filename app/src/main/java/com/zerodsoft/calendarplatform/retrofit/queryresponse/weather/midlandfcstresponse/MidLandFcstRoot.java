package com.zerodsoft.calendarplatform.retrofit.queryresponse.weather.midlandfcstresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MidLandFcstRoot implements Parcelable
{
    @Expose
    @SerializedName("response")
    private MidLandFcstResponse response;


    protected MidLandFcstRoot(Parcel in)
    {
        response = in.readParcelable(MidLandFcstResponse.class.getClassLoader());
    }

    public static final Creator<MidLandFcstRoot> CREATOR = new Creator<MidLandFcstRoot>()
    {
        @Override
        public MidLandFcstRoot createFromParcel(Parcel in)
        {
            return new MidLandFcstRoot(in);
        }

        @Override
        public MidLandFcstRoot[] newArray(int size)
        {
            return new MidLandFcstRoot[size];
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

    public void setResponse(MidLandFcstResponse response)
    {
        this.response = response;
    }

    public MidLandFcstResponse getResponse()
    {
        return response;
    }
}
