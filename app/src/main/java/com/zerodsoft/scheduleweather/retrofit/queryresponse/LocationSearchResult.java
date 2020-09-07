package com.zerodsoft.scheduleweather.retrofit.queryresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.zerodsoft.scheduleweather.activity.mapactivity.Fragment.MapController;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.addressresponse.AddressResponse;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.coordtoaddressresponse.CoordToAddress;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placecategoryresponse.PlaceCategory;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.placekeywordresponse.PlaceKeyword;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocationSearchResult implements Parcelable, Cloneable
{
    private AddressResponse addressResponse;
    private PlaceKeyword placeKeywordResponse;
    private PlaceCategory placeCategoryResponse;
    private CoordToAddress coordToAddressResponse;
    private Date downloadedDate;
    private int resultNum = 0;

    public LocationSearchResult()
    {
    }

    public LocationSearchResult(AddressResponse addressResponse, PlaceKeyword placeKeywordResponse, PlaceCategory placeCategoryResponse, CoordToAddress coordToAddressResponse, Date downloadedDate, int resultNum)
    {
        this.addressResponse = addressResponse;
        this.placeKeywordResponse = placeKeywordResponse;
        this.placeCategoryResponse = placeCategoryResponse;
        this.coordToAddressResponse = coordToAddressResponse;
        this.downloadedDate = downloadedDate;
        this.resultNum = resultNum;
    }

    protected LocationSearchResult(Parcel in)
    {
        addressResponse = in.readParcelable(AddressResponse.class.getClassLoader());
        placeKeywordResponse = in.readParcelable(PlaceKeyword.class.getClassLoader());
        placeCategoryResponse = in.readParcelable(PlaceCategory.class.getClassLoader());
        coordToAddressResponse = in.readParcelable(CoordToAddress.class.getClassLoader());
        downloadedDate = (Date) in.readSerializable();
        resultNum = in.readInt();
    }

    public static final Creator<LocationSearchResult> CREATOR = new Creator<LocationSearchResult>()
    {
        @Override
        public LocationSearchResult createFromParcel(Parcel in)
        {
            return new LocationSearchResult(in);
        }

        @Override
        public LocationSearchResult[] newArray(int size)
        {
            return new LocationSearchResult[size];
        }
    };

    public AddressResponse getAddressResponse()
    {
        return addressResponse;
    }

    public LocationSearchResult setAddressResponse(AddressResponse addressResponse)
    {
        this.addressResponse = addressResponse;
        return this;
    }

    public PlaceKeyword getPlaceKeywordResponse()
    {
        return placeKeywordResponse;
    }

    public LocationSearchResult setPlaceKeywordResponse(PlaceKeyword placeKeywordResponse)
    {
        this.placeKeywordResponse = placeKeywordResponse;
        return this;
    }

    public PlaceCategory getPlaceCategoryResponse()
    {
        return placeCategoryResponse;
    }

    public LocationSearchResult setPlaceCategoryResponse(PlaceCategory placeCategoryResponse)
    {
        this.placeCategoryResponse = placeCategoryResponse;
        return this;
    }

    public Date getDownloadedDate()
    {
        return downloadedDate;
    }

    public LocationSearchResult setDownloadedDate(Date downloadedDate)
    {
        this.downloadedDate = downloadedDate;
        return this;
    }

    public int getResultNum()
    {
        return resultNum;
    }

    public LocationSearchResult setResultNum(int resultNum)
    {
        this.resultNum = resultNum;
        return this;
    }

    public void setCoordToAddressResponse(CoordToAddress coordToAddressResponse)
    {
        this.coordToAddressResponse = coordToAddressResponse;
    }

    public CoordToAddress getCoordToAddressResponse()
    {
        return coordToAddressResponse;
    }

    public LocationSearchResult copy()
    {
        return new LocationSearchResult();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeParcelable(addressResponse, i);
        parcel.writeParcelable(placeKeywordResponse, i);
        parcel.writeParcelable(placeCategoryResponse, i);
        parcel.writeParcelable(coordToAddressResponse, i);
        parcel.writeSerializable(downloadedDate);
        parcel.writeInt(resultNum);
    }

    public List<Integer> getResultTypes()
    {
        // 검색된 타입들을 배열로 반환
        List<Integer> types = new ArrayList<>();

        if (addressResponse.getAddressResponseDocumentsList().size() != 0)
        {
            types.add(MapController.TYPE_ADDRESS);
        }
        if (placeKeywordResponse.getPlaceKeywordDocuments().size() != 0)
        {
            types.add(MapController.TYPE_PLACE_KEYWORD);
        }
        if (placeCategoryResponse.getPlaceCategoryDocuments().size() != 0)
        {
            types.add(MapController.TYPE_PLACE_CATEGORY);
        }
        return types;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
