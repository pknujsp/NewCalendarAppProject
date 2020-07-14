package com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceKeyword implements Parcelable
{
    @SerializedName("meta")
    @Expose
    private PlaceKeywordMeta placeKeywordMeta;

    @SerializedName("documents")
    @Expose
    private List<PlaceKeywordDocuments> placeKeywordDocuments;

    protected PlaceKeyword(Parcel in)
    {
        placeKeywordMeta = in.readParcelable(PlaceKeywordMeta.class.getClassLoader());
        placeKeywordDocuments = in.createTypedArrayList(PlaceKeywordDocuments.CREATOR);
    }

    public static final Creator<PlaceKeyword> CREATOR = new Creator<PlaceKeyword>()
    {
        @Override
        public PlaceKeyword createFromParcel(Parcel in)
        {
            return new PlaceKeyword(in);
        }

        @Override
        public PlaceKeyword[] newArray(int size)
        {
            return new PlaceKeyword[size];
        }
    };

    public PlaceKeywordMeta getPlaceKeywordMeta()
    {
        return placeKeywordMeta;
    }

    public void setPlaceKeywordMeta(PlaceKeywordMeta placeKeywordMeta)
    {
        this.placeKeywordMeta = placeKeywordMeta;
    }

    public List<PlaceKeywordDocuments> getPlaceKeywordDocuments()
    {
        return placeKeywordDocuments;
    }

    public void setPlaceKeywordDocuments(List<PlaceKeywordDocuments> placeKeywordDocuments)
    {
        this.placeKeywordDocuments = placeKeywordDocuments;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeParcelable(placeKeywordMeta, i);
        parcel.writeTypedList(placeKeywordDocuments);
    }
}
