package com.zerodsoft.scheduleweather.room.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "location_table")
public class LocationDTO implements Parcelable
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "calendar_id")
    private int calendarId;

    @ColumnInfo(name = "account_name")
    private String accountName;

    @ColumnInfo(name = "event_id")
    private int eventId;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "address_name")
    private String addressName;

    @ColumnInfo(name = "place_id")
    private String placeId;

    @ColumnInfo(name = "place_name")
    private String placeName;

    public LocationDTO()
    {
    }

    protected LocationDTO(Parcel in)
    {
        id = in.readInt();
        calendarId = in.readInt();
        accountName = in.readString();
        eventId = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        addressName = in.readString();
        placeId = in.readString();
        placeName = in.readString();
    }

    public static final Creator<LocationDTO> CREATOR = new Creator<LocationDTO>()
    {
        @Override
        public LocationDTO createFromParcel(Parcel in)
        {
            return new LocationDTO(in);
        }

        @Override
        public LocationDTO[] newArray(int size)
        {
            return new LocationDTO[size];
        }
    };

    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public int getCalendarId()
    {
        return calendarId;
    }

    public void setCalendarId(int calendarId)
    {
        this.calendarId = calendarId;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    public int getEventId()
    {
        return eventId;
    }

    public void setEventId(int eventId)
    {
        this.eventId = eventId;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public String getAddressName()
    {
        return addressName;
    }

    public void setAddressName(String addressName)
    {
        this.addressName = addressName;
    }

    public String getPlaceId()
    {
        return placeId;
    }

    public void setPlaceId(String placeId)
    {
        this.placeId = placeId;
    }

    public void setPlaceName(String placeName)
    {
        this.placeName = placeName;
    }

    public String getPlaceName()
    {
        return placeName;
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
        parcel.writeInt(calendarId);
        parcel.writeString(accountName);
        parcel.writeInt(eventId);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(addressName);
        parcel.writeString(placeId);
        parcel.writeString(placeName);
    }
}
