package com.zerodsoft.scheduleweather.Room.DTO;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "TB_FAVORITE_LOC"
        , foreignKeys = @ForeignKey(entity = ScheduleDTO.class,
        parentColumns = "id",
        childColumns = "schedule_id"))
public class FavoriteLocDTO implements Parcelable
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "schedule_id")
    private int scheduleId;

    @ColumnInfo(name = "place_id")
    private int placeId;

    @ColumnInfo(name = "address_id")
    private int addressId;

    public FavoriteLocDTO()
    {
    }

    protected FavoriteLocDTO(Parcel in)
    {
        id = in.readInt();
        scheduleId = in.readInt();
        placeId = in.readInt();
        addressId = in.readInt();
    }

    public static final Creator<FavoriteLocDTO> CREATOR = new Creator<FavoriteLocDTO>()
    {
        @Override
        public FavoriteLocDTO createFromParcel(Parcel in)
        {
            return new FavoriteLocDTO(in);
        }

        @Override
        public FavoriteLocDTO[] newArray(int size)
        {
            return new FavoriteLocDTO[size];
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

    public int getPlaceId()
    {
        return placeId;
    }

    public void setPlaceId(int placeId)
    {
        this.placeId = placeId;
    }

    public int getAddressId()
    {
        return addressId;
    }

    public void setAddressId(int addressId)
    {
        this.addressId = addressId;
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
        parcel.writeInt(placeId);
        parcel.writeInt(addressId);
    }
}
