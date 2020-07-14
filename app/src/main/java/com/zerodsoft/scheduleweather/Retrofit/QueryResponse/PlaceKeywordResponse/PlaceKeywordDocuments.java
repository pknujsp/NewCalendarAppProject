package com.zerodsoft.scheduleweather.Retrofit.QueryResponse.PlaceKeywordResponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PlaceKeywordDocuments implements Parcelable
{
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("place_name")
    @Expose
    private String placeName;

    @SerializedName("category_name")
    @Expose
    private String categoryName;

    @SerializedName("category_group_code")
    @Expose
    private String categoryGroupCode;

    @SerializedName("category_group_name")
    @Expose
    private String categoryGroupName;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("address_name")
    @Expose
    private String addressName;

    @SerializedName("road_address_name")
    @Expose
    private String roadAddressName;

    @SerializedName("x")
    @Expose
    private double x;

    @SerializedName("y")
    @Expose
    private double y;

    @SerializedName("place_url")
    @Expose
    private String placeUrl;

    @SerializedName("distance")
    @Expose
    private String distance;

    protected PlaceKeywordDocuments(Parcel in)
    {
        id = in.readString();
        placeName = in.readString();
        categoryName = in.readString();
        categoryGroupCode = in.readString();
        categoryGroupName = in.readString();
        phone = in.readString();
        addressName = in.readString();
        roadAddressName = in.readString();
        x = in.readDouble();
        y = in.readDouble();
        placeUrl = in.readString();
        distance = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(placeName);
        dest.writeString(categoryName);
        dest.writeString(categoryGroupCode);
        dest.writeString(categoryGroupName);
        dest.writeString(phone);
        dest.writeString(addressName);
        dest.writeString(roadAddressName);
        dest.writeDouble(x);
        dest.writeDouble(y);
        dest.writeString(placeUrl);
        dest.writeString(distance);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<PlaceKeywordDocuments> CREATOR = new Creator<PlaceKeywordDocuments>()
    {
        @Override
        public PlaceKeywordDocuments createFromParcel(Parcel in)
        {
            return new PlaceKeywordDocuments(in);
        }

        @Override
        public PlaceKeywordDocuments[] newArray(int size)
        {
            return new PlaceKeywordDocuments[size];
        }
    };

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPlaceName()
    {
        return placeName;
    }

    public void setPlaceName(String placeName)
    {
        this.placeName = placeName;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String getCategoryGroupCode()
    {
        return categoryGroupCode;
    }

    public void setCategoryGroupCode(String categoryGroupCode)
    {
        this.categoryGroupCode = categoryGroupCode;
    }

    public String getCategoryGroupName()
    {
        return categoryGroupName;
    }

    public void setCategoryGroupName(String categoryGroupName)
    {
        this.categoryGroupName = categoryGroupName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getAddressName()
    {
        return addressName;
    }

    public void setAddressName(String addressName)
    {
        this.addressName = addressName;
    }

    public String getRoadAddressName()
    {
        return roadAddressName;
    }

    public void setRoadAddressName(String roadAddressName)
    {
        this.roadAddressName = roadAddressName;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public String getPlaceUrl()
    {
        return placeUrl;
    }

    public void setPlaceUrl(String placeUrl)
    {
        this.placeUrl = placeUrl;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }
}
