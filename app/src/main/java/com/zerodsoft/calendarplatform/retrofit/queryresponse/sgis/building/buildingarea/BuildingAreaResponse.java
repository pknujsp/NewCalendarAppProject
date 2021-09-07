package com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.building.buildingarea;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.calendarplatform.retrofit.queryresponse.sgis.building.SgisBuildingRoot;

import java.util.List;

public class BuildingAreaResponse extends SgisBuildingRoot implements Parcelable
{
    @Expose
    @SerializedName("result")
    private List<BuildingAreaItem> result;

    protected BuildingAreaResponse(Parcel in)
    {
        result = in.createTypedArrayList(BuildingAreaItem.CREATOR);
    }

    public static final Creator<BuildingAreaResponse> CREATOR = new Creator<BuildingAreaResponse>()
    {
        @Override
        public BuildingAreaResponse createFromParcel(Parcel in)
        {
            return new BuildingAreaResponse(in);
        }

        @Override
        public BuildingAreaResponse[] newArray(int size)
        {
            return new BuildingAreaResponse[size];
        }
    };

    public List<BuildingAreaItem> getResult()
    {
        return result;
    }

    public void setResult(List<BuildingAreaItem> result)
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
