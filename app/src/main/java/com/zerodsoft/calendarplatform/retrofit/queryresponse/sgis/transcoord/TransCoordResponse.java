package com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.transcoord;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransCoordResponse implements Parcelable
{
    @Expose
    @SerializedName("result")
    private TransCoordResult result;

    protected TransCoordResponse(Parcel in)
    {
        result = in.readParcelable(TransCoordResult.class.getClassLoader());
    }

    public static final Creator<TransCoordResponse> CREATOR = new Creator<TransCoordResponse>()
    {
        @Override
        public TransCoordResponse createFromParcel(Parcel in)
        {
            return new TransCoordResponse(in);
        }

        @Override
        public TransCoordResponse[] newArray(int size)
        {
            return new TransCoordResponse[size];
        }
    };

    public void setResult(TransCoordResult result)
    {
        this.result = result;
    }

    public TransCoordResult getResult()
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
