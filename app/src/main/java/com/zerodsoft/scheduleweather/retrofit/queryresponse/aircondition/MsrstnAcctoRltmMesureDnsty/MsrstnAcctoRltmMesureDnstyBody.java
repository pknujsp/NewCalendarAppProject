package com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.MsrstnAcctoRltmMesureDnsty;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.AirConditionRoot;

import java.io.Serializable;
import java.util.List;

public class MsrstnAcctoRltmMesureDnstyBody extends AirConditionRoot implements Parcelable, Serializable
{
    @Expose
    @SerializedName("items")
    private List<MsrstnAcctoRltmMesureDnstyItem> item;

    protected MsrstnAcctoRltmMesureDnstyBody(Parcel in)
    {
        item = in.createTypedArrayList(MsrstnAcctoRltmMesureDnstyItem.CREATOR);
    }

    public static final Creator<MsrstnAcctoRltmMesureDnstyBody> CREATOR = new Creator<MsrstnAcctoRltmMesureDnstyBody>()
    {
        @Override
        public MsrstnAcctoRltmMesureDnstyBody createFromParcel(Parcel in)
        {
            return new MsrstnAcctoRltmMesureDnstyBody(in);
        }

        @Override
        public MsrstnAcctoRltmMesureDnstyBody[] newArray(int size)
        {
            return new MsrstnAcctoRltmMesureDnstyBody[size];
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
        parcel.writeTypedList(item);
    }

    public void setItem(List<MsrstnAcctoRltmMesureDnstyItem> item)
    {
        this.item = item;
    }

    public List<MsrstnAcctoRltmMesureDnstyItem> getItem()
    {
        return item;
    }
}
