package com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceCategory implements Parcelable
{
    @SerializedName("meta")
    @Expose
    private PlaceCategoryMeta placeCategoryMeta;

    @SerializedName("documents")
    @Expose
    private List<PlaceCategoryDocuments> placeCategoryDocuments;

    protected PlaceCategory(Parcel in)
    {
        placeCategoryMeta = in.readParcelable(PlaceCategoryMeta.class.getClassLoader());
        placeCategoryDocuments = in.createTypedArrayList(PlaceCategoryDocuments.CREATOR);
    }

    public static final Creator<PlaceCategory> CREATOR = new Creator<PlaceCategory>()
    {
        @Override
        public PlaceCategory createFromParcel(Parcel in)
        {
            return new PlaceCategory(in);
        }

        @Override
        public PlaceCategory[] newArray(int size)
        {
            return new PlaceCategory[size];
        }
    };

    public PlaceCategoryMeta getPlaceCategoryMeta()
    {
        return placeCategoryMeta;
    }

    public void setPlaceCategoryMeta(PlaceCategoryMeta placeCategoryMeta)
    {
        this.placeCategoryMeta = placeCategoryMeta;
    }

    public List<PlaceCategoryDocuments> getPlaceCategoryDocuments()
    {
        return placeCategoryDocuments;
    }

    public void setPlaceCategoryDocuments(List<PlaceCategoryDocuments> placeCategoryDocuments)
    {
        this.placeCategoryDocuments = placeCategoryDocuments;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeParcelable(placeCategoryMeta, i);
        parcel.writeTypedList(placeCategoryDocuments);
    }
}
