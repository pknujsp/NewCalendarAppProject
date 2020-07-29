package com.zerodsoft.scheduleweather.Retrofit.QueryResponse;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponse.AddressResponseMeta;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceCategoryResponse.PlaceCategoryMeta;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse.PlaceKeywordMeta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddressSearchResult implements Parcelable
{
    private List<AddressResponseDocuments> addressResponseDocuments;
    private List<PlaceKeywordDocuments> placeKeywordDocuments;
    private List<PlaceCategoryDocuments> placeCategoryDocuments;

    private AddressResponseMeta addressResponseMeta;
    private PlaceKeywordMeta placeKeywordMeta;
    private PlaceCategoryMeta placeCategoryMeta;

    private int resultNum;
    private long downloadedTime;

    public AddressSearchResult()
    {
        resultNum = 0;
        downloadedTime = System.currentTimeMillis();
        addressResponseDocuments = new ArrayList<>();
        placeKeywordDocuments = new ArrayList<>();
        placeCategoryDocuments = new ArrayList<>();
    }

    public AddressSearchResult(int resultNum, long downloadedTime, List<AddressResponseDocuments> addressResponseDocuments,
                               List<PlaceKeywordDocuments> placeKeywordDocuments,
                               List<PlaceCategoryDocuments> placeCategoryDocuments, AddressResponseMeta addressResponseMeta,
                               PlaceKeywordMeta placeKeywordMeta, PlaceCategoryMeta placeCategoryMeta)
    {
        this.resultNum = resultNum;
        this.downloadedTime = downloadedTime;
        this.addressResponseDocuments = addressResponseDocuments;
        this.placeKeywordDocuments = placeKeywordDocuments;
        this.placeCategoryDocuments = placeCategoryDocuments;
        this.addressResponseMeta = addressResponseMeta;
        this.placeKeywordMeta = placeKeywordMeta;
        this.placeCategoryMeta = placeCategoryMeta;
    }

    protected AddressSearchResult(Parcel in)
    {
        addressResponseDocuments = in.createTypedArrayList(AddressResponseDocuments.CREATOR);
        placeKeywordDocuments = in.createTypedArrayList(PlaceKeywordDocuments.CREATOR);
        placeCategoryDocuments = in.createTypedArrayList(PlaceCategoryDocuments.CREATOR);
        addressResponseMeta = in.readParcelable(AddressResponseMeta.class.getClassLoader());
        placeKeywordMeta = in.readParcelable(PlaceKeywordMeta.class.getClassLoader());
        placeCategoryMeta = in.readParcelable(PlaceCategoryMeta.class.getClassLoader());
        resultNum = in.readInt();
        downloadedTime = in.readLong();
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

    public AddressSearchResult setAddressResponseMeta(AddressResponseMeta addressResponseMeta)
    {
        this.addressResponseMeta = addressResponseMeta;
        return this;
    }

    public AddressSearchResult setPlaceKeywordMeta(PlaceKeywordMeta placeKeywordMeta)
    {
        this.placeKeywordMeta = placeKeywordMeta;
        return this;
    }

    public AddressSearchResult setPlaceCategoryMeta(PlaceCategoryMeta placeCategoryMeta)
    {
        this.placeCategoryMeta = placeCategoryMeta;
        return this;
    }

    public AddressResponseMeta getAddressResponseMeta()
    {
        return addressResponseMeta;
    }

    public PlaceKeywordMeta getPlaceKeywordMeta()
    {
        return placeKeywordMeta;
    }

    public PlaceCategoryMeta getPlaceCategoryMeta()
    {
        return placeCategoryMeta;
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

    public long getDownloadedTime()
    {
        return downloadedTime;
    }

    public AddressSearchResult clone()
    {
        List<AddressResponseDocuments> newAddressResponseDocuments = new ArrayList<>(addressResponseDocuments);
        List<PlaceKeywordDocuments> newPlaceKeywordDocuments = new ArrayList<>(placeKeywordDocuments);
        List<PlaceCategoryDocuments> newPlaceCategoryDocuments = new ArrayList<>(placeCategoryDocuments);

        Collections.copy(newAddressResponseDocuments, addressResponseDocuments);
        Collections.copy(newPlaceKeywordDocuments, placeKeywordDocuments);
        Collections.copy(newPlaceCategoryDocuments, placeCategoryDocuments);

        return new AddressSearchResult(resultNum, downloadedTime, newAddressResponseDocuments, newPlaceKeywordDocuments, newPlaceCategoryDocuments, addressResponseMeta,
                placeKeywordMeta, placeCategoryMeta);
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
        parcel.writeParcelable(addressResponseMeta, i);
        parcel.writeParcelable(placeKeywordMeta, i);
        parcel.writeParcelable(placeCategoryMeta, i);
        parcel.writeInt(resultNum);
        parcel.writeLong(downloadedTime);
    }

    public List<Integer> getResultTypes()
    {
        // 검색된 타입들을 배열로 반환
        List<Integer> types = new ArrayList<>();

        if (addressResponseDocuments.size() != 0)
        {
            types.add(DownloadData.ADDRESS);
        }
        if (placeKeywordDocuments.size() != 0)
        {
            types.add(DownloadData.PLACE_KEYWORD);
        }
        if (placeCategoryDocuments.size() != 0)
        {
            types.add(DownloadData.PLACE_CATEGORY);
        }
        return types;
    }
}
