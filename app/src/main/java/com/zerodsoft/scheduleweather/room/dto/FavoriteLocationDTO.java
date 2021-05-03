package com.zerodsoft.scheduleweather.room.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "favorite_location_table")
public class FavoriteLocationDTO
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer id;

    @ColumnInfo(name = "place_name")
    private String placeName;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "place_id")
    private String placeId;

    @ColumnInfo(name = "latitude")
    private String latitude;

    @ColumnInfo(name = "longitude")
    private String longitude;

    @ColumnInfo(name = "type")
    private Integer type;

    @Ignore
    public static final int RESTAURANT = 0;
    @Ignore
    public static final int PLACE = 1;
    @Ignore
    public static final int ADDRESS = 2;
    @Ignore
    public static final int ONLY_FOR_MAP = 3;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
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

    public String getPlaceId()
    {
        return placeId;
    }

    public void setPlaceId(String placeId)
    {
        this.placeId = placeId;
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

    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getType()
    {
        return type;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FavoriteLocationDTO that = (FavoriteLocationDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(placeName, that.placeName) &&
                Objects.equals(address, that.address) &&
                Objects.equals(placeId, that.placeId) &&
                Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, placeName, address, placeId, latitude, longitude, type);
    }
}
