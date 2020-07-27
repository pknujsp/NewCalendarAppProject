package com.zerodsoft.scheduleweather.Room.DTO;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "TB_ADDRESS",
        foreignKeys = @ForeignKey(
                entity = ScheduleDTO.class,
                parentColumns = "id",
                childColumns = "schedule_id"
        ))

public class AddressDTO implements Parcelable
{
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "schedule_id")
    private int scheduleId;

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

    public AddressDTO()
    {

    }

    public AddressDTO(Parcel in)
    {
        id = in.readInt();
        scheduleId = in.readInt();
        addressName = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        weatherX = in.readString();
        weatherY = in.readString();
    }

    public static final Creator<AddressDTO> CREATOR = new Creator<AddressDTO>()
    {
        @Override
        public AddressDTO createFromParcel(Parcel in)
        {
            return new AddressDTO(in);
        }

        @Override
        public AddressDTO[] newArray(int size)
        {
            return new AddressDTO[size];
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

    public String getAddressName()
    {
        return addressName;
    }

    public void setAddressName(String addressName)
    {
        this.addressName = addressName;
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
        parcel.writeString(addressName);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(weatherX);
        parcel.writeString(weatherY);
    }
}
