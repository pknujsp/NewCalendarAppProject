package com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.NearbyMsrstnList;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.aircondition.FindStationRoot;

import java.util.List;

public class NearbyMsrstnListBody extends FindStationRoot implements Parcelable
{
    @Expose
    @SerializedName("items")
    private List<NearbyMsrstnListItem> items;


    protected NearbyMsrstnListBody(Parcel in)
    {
        items = in.createTypedArrayList(NearbyMsrstnListItem.CREATOR);
    }

    public static final Creator<NearbyMsrstnListBody> CREATOR = new Creator<NearbyMsrstnListBody>()
    {
        @Override
        public NearbyMsrstnListBody createFromParcel(Parcel in)
        {
            return new NearbyMsrstnListBody(in);
        }

        @Override
        public NearbyMsrstnListBody[] newArray(int size)
        {
            return new NearbyMsrstnListBody[size];
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
        parcel.writeTypedList(items);
    }

    public void setItems(List<NearbyMsrstnListItem> items)
    {
        this.items = items;
    }

    public List<NearbyMsrstnListItem> getItems()
    {
        return items;
    }
}
