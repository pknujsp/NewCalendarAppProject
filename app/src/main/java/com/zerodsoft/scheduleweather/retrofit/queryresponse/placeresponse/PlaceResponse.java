package com.zerodsoft.scheduleweather.retrofit.queryresponse.placeresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceResponse implements Parcelable
{
    @SerializedName("meta")
    @Expose
    private PlaceMeta placeMeta;

    @SerializedName("documents")
    @Expose
    private List<PlaceDocuments> placeDocuments;

    protected PlaceResponse(Parcel in)
    {
        placeMeta = in.readParcelable(PlaceMeta.class.getClassLoader());
        placeDocuments = in.createTypedArrayList(PlaceDocuments.CREATOR);
    }

    public static final Creator<PlaceResponse> CREATOR = new Creator<PlaceResponse>()
    {
        @Override
        public PlaceResponse createFromParcel(Parcel in)
        {
            return new PlaceResponse(in);
        }

        @Override
        public PlaceResponse[] newArray(int size)
        {
            return new PlaceResponse[size];
        }
    };

    public PlaceMeta getPlaceMeta()
    {
        return placeMeta;
    }

    public void setPlaceMeta(PlaceMeta placeMeta)
    {
        this.placeMeta = placeMeta;
    }

    public List<PlaceDocuments> getPlaceDocuments()
    {
        return placeDocuments;
    }

    public void setPlaceDocuments(List<PlaceDocuments> placeDocuments)
    {
        this.placeDocuments = placeDocuments;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeParcelable(placeMeta, i);
        parcel.writeTypedList(placeDocuments);
    }
}
