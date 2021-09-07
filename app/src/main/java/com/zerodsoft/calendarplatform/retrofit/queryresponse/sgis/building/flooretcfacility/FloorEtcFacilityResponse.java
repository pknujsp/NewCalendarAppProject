package com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.building.flooretcfacility;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.building.SgisBuildingRoot;

import java.util.List;

public class FloorEtcFacilityResponse extends SgisBuildingRoot implements Parcelable
{
    @Expose
    @SerializedName("result")
    private List<FloorEtcFacilityItem> result;

    protected FloorEtcFacilityResponse(Parcel in)
    {
        result = in.createTypedArrayList(FloorEtcFacilityItem.CREATOR);
    }

    public static final Creator<FloorEtcFacilityResponse> CREATOR = new Creator<FloorEtcFacilityResponse>()
    {
        @Override
        public FloorEtcFacilityResponse createFromParcel(Parcel in)
        {
            return new FloorEtcFacilityResponse(in);
        }

        @Override
        public FloorEtcFacilityResponse[] newArray(int size)
        {
            return new FloorEtcFacilityResponse[size];
        }
    };

    public List<FloorEtcFacilityItem> getResult()
    {
        return result;
    }

    public void setResult(List<FloorEtcFacilityItem> result)
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
        parcel.writeTypedList(result);
    }
}
