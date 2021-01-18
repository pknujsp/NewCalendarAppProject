package com.zerodsoft.scheduleweather.room.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(tableName = "TB_PLACE")
public class PlaceDTO extends LocationDTO implements Parcelable, Cloneable
{
    @ColumnInfo(name = "id", index = true)
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "schedule_id", index = true)
    private int scheduleId;

    @ColumnInfo(name = "place_id")
    private String placeId;

    @ColumnInfo(name = "place_name")
    private String placeName;

    @ColumnInfo(name = "address_name")
    private String addressName;

    @ColumnInfo(name = "latitude")
    private String latitude;

    @ColumnInfo(name = "longitude")
    private String longitude;

    @ColumnInfo(name = "weather_x")
    private String weatherX;

    @ColumnInfo(name = "weather_y")
    private String weatherY;

    public PlaceDTO()
    {
        super();
    }

    public PlaceDTO(Parcel in)
    {
        super();
        id = in.readInt();
        scheduleId = in.readInt();
        placeId = in.readString();
        placeName = in.readString();
        addressName = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        weatherX = in.readString();
        weatherY = in.readString();
    }

    public static final Creator<PlaceDTO> CREATOR = new Creator<PlaceDTO>()
    {
        @Override
        public PlaceDTO createFromParcel(Parcel in)
        {
            return new PlaceDTO(in);
        }

        @Override
        public PlaceDTO[] newArray(int size)
        {
            return new PlaceDTO[size];
        }
    };

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getScheduleId()
    {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId)
    {
        this.scheduleId = scheduleId;
    }

    public String getPlaceId()
    {
        return placeId;
    }

    public void setPlaceId(String placeId)
    {
        this.placeId = placeId;
    }

    public String getPlaceName()
    {
        return placeName;
    }

    public void setPlaceName(String placeName)
    {
        this.placeName = placeName;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getWeatherX()
    {
        return weatherX;
    }

    public void setWeatherX(String weatherX)
    {
        this.weatherX = weatherX;
    }

    public String getWeatherY()
    {
        return weatherY;
    }

    public void setWeatherY(String weatherY)
    {
        this.weatherY = weatherY;
    }

    public void setAddressName(String addressName)
    {
        this.addressName = addressName;
    }

    public String getAddressName()
    {
        return addressName;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(id);
        parcel.writeInt(scheduleId);
        parcel.writeString(placeId);
        parcel.writeString(placeName);
        parcel.writeString(addressName);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(weatherX);
        parcel.writeString(weatherY);
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}