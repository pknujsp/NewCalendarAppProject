package com.zerodsoft.scheduleweather.room.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.zerodsoft.scheduleweather.etc.LocationType;

import java.util.Objects;

@Entity(tableName = "location_table")
public class LocationDTO implements Parcelable, Cloneable
{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "calendar_id")
    private int calendarId;

    @ColumnInfo(name = "event_id")
    private long eventId;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "address_name")
    private String addressName;

    @ColumnInfo(name = "road_address_name")
    private String roadAddressName;

    @ColumnInfo(name = "place_id")
    private String placeId;

    @ColumnInfo(name = "place_name")
    private String placeName;

    @ColumnInfo(name = "location_type")
    private Integer locationType = LocationType.PLACE;

    @Ignore
    public LocationDTO()
    {
    }

    public LocationDTO(int id, int calendarId, long eventId, double latitude, double longitude, String addressName, String roadAddressName, String placeId, String placeName, int locationType)
    {
        this.id = id;
        this.calendarId = calendarId;
        this.eventId = eventId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.placeId = placeId;
        this.placeName = placeName;
        this.locationType = locationType;
    }

    protected LocationDTO(Parcel in)
    {
        id = in.readInt();
        calendarId = in.readInt();
        eventId = in.readLong();
        latitude = in.readDouble();
        longitude = in.readDouble();
        addressName = in.readString();
        roadAddressName = in.readString();
        placeId = in.readString();
        placeName = in.readString();
        locationType = in.readInt();
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
        parcel.writeLong(eventId);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(addressName);
        parcel.writeString(roadAddressName);
        parcel.writeString(placeId);
        parcel.writeString(placeName);
        parcel.writeInt(locationType);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LocationDTO that = (LocationDTO) o;
        return id == that.id &&
                calendarId == that.calendarId &&
                eventId == that.eventId &&
                Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Objects.equals(addressName, that.addressName) &&
                Objects.equals(roadAddressName, that.roadAddressName) &&
                Objects.equals(placeId, that.placeId) &&
                Objects.equals(placeName, that.placeName) &&
                Objects.equals(locationType, that.locationType);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, calendarId, eventId, latitude, longitude, addressName, roadAddressName, placeId, placeName, locationType);
    }

    @NonNull
    @Override
    public LocationDTO clone() throws CloneNotSupportedException
    {
        return (LocationDTO) super.clone();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getCalendarId()
    {
        return calendarId;
    }

    public void setCalendarId(int calendarId)
    {
        this.calendarId = calendarId;
    }

    public long getEventId()
    {
        return eventId;
    }

    public void setEventId(long eventId)
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

    public String getRoadAddressName()
    {
        return roadAddressName;
    }

    public void setRoadAddressName(String roadAddressName)
    {
        this.roadAddressName = roadAddressName;
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

    public void setLocationType(Integer locationType)
    {
        this.locationType = locationType;
    }

    public Integer getLocationType()
    {
        return locationType;
    }

    public boolean isEmpty()
    {
        return latitude == 0 ? true : false;
    }
}
