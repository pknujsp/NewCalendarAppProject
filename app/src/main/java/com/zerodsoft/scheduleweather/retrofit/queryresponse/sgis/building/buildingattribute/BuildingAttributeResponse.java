package com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingattribute;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.sgis.building.buildingarea.BuildingAreaItem;

import java.util.List;

public class BuildingAttributeResponse implements Parcelable
{
    @Expose
    @SerializedName("result")
    private BuildingAttributeResult result;

    protected BuildingAttributeResponse(Parcel in)
    {
        result = in.readParcelable(BuildingAttributeResult.class.getClassLoader());
    }

    public static final Creator<BuildingAttributeResponse> CREATOR = new Creator<BuildingAttributeResponse>()
    {
        @Override
        public BuildingAttributeResponse createFromParcel(Parcel in)
        {
            return new BuildingAttributeResponse(in);
        }

        @Override
        public BuildingAttributeResponse[] newArray(int size)
        {
            return new BuildingAttributeResponse[size];
        }
    };

    public BuildingAttributeResult getResult()
    {
        return result;
    }

    public void setResult(BuildingAttributeResult result)
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
