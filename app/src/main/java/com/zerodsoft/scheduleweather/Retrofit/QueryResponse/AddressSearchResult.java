package com.zerodsoft.scheduleweather.Retrofit.QueryResponse;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordDocuments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddressSearchResult implements Cloneable, Parcelable
{
    private List<AddressResponseDocuments> addressResponseDocuments;
    private List<PlaceKeywordDocuments> placeKeywordDocuments;
    private List<PlaceCategoryDocuments> placeCategoryDocuments;
    private int resultNum;

    public AddressSearchResult()
    {
        resultNum = 0;
        addressResponseDocuments = new ArrayList<>();
        placeKeywordDocuments = new ArrayList<>();
        placeCategoryDocuments = new ArrayList<>();
    }

    public AddressSearchResult(int resultNum, List<AddressResponseDocuments> addressResponseDocuments,
                               List<PlaceKeywordDocuments> placeKeywordDocuments,
                               List<PlaceCategoryDocuments> placeCategoryDocuments)
    {
        this.resultNum = resultNum;
        this.addressResponseDocuments = addressResponseDocuments;
        this.placeKeywordDocuments = placeKeywordDocuments;
        this.placeCategoryDocuments = placeCategoryDocuments;
    }

    protected AddressSearchResult(Parcel in)
    {
        addressResponseDocuments = in.createTypedArrayList(AddressResponseDocuments.CREATOR);
        placeKeywordDocuments = in.createTypedArrayList(PlaceKeywordDocuments.CREATOR);
        placeCategoryDocuments = in.createTypedArrayList(PlaceCategoryDocuments.CREATOR);
        resultNum = in.readInt();
    }

    public static final Creator<AddressSearchResult> CREATOR = new Creator<AddressSearchResult>()
    {
        @Override
        public AddressSearchResult createFromParcel(Parcel in)
        {
            return new AddressSearchResult(in);
        }

        @Override
        public AddressSearchResult[] newArray(int size)
        {
            return new AddressSearchResult[size];
        }
    };

    public AddressSearchResult setAddressResponseDocuments(List<AddressResponseDocuments> addressResponseDocuments)
    {
        this.addressResponseDocuments = addressResponseDocuments;
        resultNum++;
        return this;
    }

    public AddressSearchResult setPlaceKeywordDocuments(List<PlaceKeywordDocuments> placeKeywordDocuments)
    {
        this.placeKeywordDocuments = placeKeywordDocuments;
        resultNum++;
        return this;
    }

    public AddressSearchResult setPlaceCategoryDocuments(List<PlaceCategoryDocuments> placeCategoryDocuments)
    {
        this.placeCategoryDocuments = placeCategoryDocuments;
        resultNum++;
        return this;
    }

    public int getResultNum()
    {
        return resultNum;
    }

    public List<AddressResponseDocuments> getAddressResponseDocuments()
    {
        return addressResponseDocuments;
    }

    public List<PlaceCategoryDocuments> getPlaceCategoryDocuments()
    {
        return placeCategoryDocuments;
    }

    public List<PlaceKeywordDocuments> getPlaceKeywordDocuments()
    {
        return placeKeywordDocuments;
    }

    public void clearAll()
    {
        resultNum = 0;
        addressResponseDocuments.clear();
        placeKeywordDocuments.clear();
        placeCategoryDocuments.clear();
    }


    public AddressSearchResult clone()
    {
        List<AddressResponseDocuments> newAddressResponseDocuments = new ArrayList<>(addressResponseDocuments);
        List<PlaceKeywordDocuments> newPlaceKeywordDocuments = new ArrayList<>(placeKeywordDocuments);
        List<PlaceCategoryDocuments> newPlaceCategoryDocuments = new ArrayList<>(placeCategoryDocuments);

        Collections.copy(newAddressResponseDocuments, addressResponseDocuments);
        Collections.copy(newPlaceKeywordDocuments, placeKeywordDocuments);
        Collections.copy(newPlaceCategoryDocuments, placeCategoryDocuments);

        return new AddressSearchResult(resultNum, newAddressResponseDocuments, newPlaceKeywordDocuments, newPlaceCategoryDocuments);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeTypedList(addressResponseDocuments);
        parcel.writeTypedList(placeKeywordDocuments);
        parcel.writeTypedList(placeCategoryDocuments);
        parcel.writeInt(resultNum);
    }
}
