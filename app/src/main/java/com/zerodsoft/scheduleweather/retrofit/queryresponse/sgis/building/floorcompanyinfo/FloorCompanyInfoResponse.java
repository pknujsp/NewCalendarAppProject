package com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.floorcompanyinfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.SgisBuildingRoot;

public class FloorCompanyInfoResponse extends SgisBuildingRoot implements Parcelable
{
    @Expose
    @SerializedName("result")
    private FloorCompanyInfoResult result;

    protected FloorCompanyInfoResponse(Parcel in)
    {
        result = in.readParcelable(FloorCompanyInfoResult.class.getClassLoader());
    }

    public static final Creator<FloorCompanyInfoResponse> CREATOR = new Creator<FloorCompanyInfoResponse>()
    {
        @Override
        public FloorCompanyInfoResponse createFromParcel(Parcel in)
        {
            return new FloorCompanyInfoResponse(in);
        }

        @Override
        public FloorCompanyInfoResponse[] newArray(int size)
        {
            return new FloorCompanyInfoResponse[size];
        }
    };

    public FloorCompanyInfoResult getResult()
    {
        return result;
    }

    public void setResult(FloorCompanyInfoResult result)
    {
        this.result = result;
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
