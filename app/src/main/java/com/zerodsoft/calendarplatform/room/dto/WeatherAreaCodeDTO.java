package com.zerodsoft.calendarplatform.room.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weather_area_code_table")
public class WeatherAreaCodeDTO implements Parcelable
{
    @ColumnInfo(name = "administrative_area_code")
    @PrimaryKey
    @NonNull
    private String administrativeAreaCode;

    @ColumnInfo(name = "phase1")
    private String phase1;

    @ColumnInfo(name = "phase2")
    private String phase2;

    @ColumnInfo(name = "phase3")
    private String phase3;

    @ColumnInfo(name = "x")
    private String x;

    @ColumnInfo(name = "y")
    private String y;

    @ColumnInfo(name = "longitude_hours")
    private String longitudeHours;

    @ColumnInfo(name = "longitude_minutes")
    private String longitudeMinutes;

    @ColumnInfo(name = "longitude_seconds")
    private String longitudeSeconds;

    @ColumnInfo(name = "latitude_hours")
    private String latitudeHours;

    @ColumnInfo(name = "latitude_minutes")
    private String latitudeMinutes;

    @ColumnInfo(name = "latitude_seconds")
    private String latitudeSeconds;

    @ColumnInfo(name = "longitude_seconds_divide_100")
    private String longitudeSecondsDivide100;

    @ColumnInfo(name = "latitude_seconds_divide_100")
    private String latitudeSecondsDivide100;

    @ColumnInfo(name = "mid_land_fcst_code")
    private String midLandFcstCode;

    @ColumnInfo(name = "mid_ta_code")
    private String midTaCode;

    public WeatherAreaCodeDTO()
    {
    }

    protected WeatherAreaCodeDTO(Parcel in)
    {
        administrativeAreaCode = in.readString();
        phase1 = in.readString();
        phase2 = in.readString();
        phase3 = in.readString();
        x = in.readString();
        y = in.readString();
        longitudeHours = in.readString();
        longitudeMinutes = in.readString();
        longitudeSeconds = in.readString();
        latitudeHours = in.readString();
        latitudeMinutes = in.readString();
        latitudeSeconds = in.readString();
        longitudeSecondsDivide100 = in.readString();
        latitudeSecondsDivide100 = in.readString();
        midLandFcstCode = in.readString();
        midTaCode = in.readString();
    }

    public static final Creator<WeatherAreaCodeDTO> CREATOR = new Creator<WeatherAreaCodeDTO>()
    {
        @Override
        public WeatherAreaCodeDTO createFromParcel(Parcel in)
        {
            return new WeatherAreaCodeDTO(in);
        }

        @Override
        public WeatherAreaCodeDTO[] newArray(int size)
        {
            return new WeatherAreaCodeDTO[size];
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
        parcel.writeString(administrativeAreaCode);
        parcel.writeString(phase1);
        parcel.writeString(phase2);
        parcel.writeString(phase3);
        parcel.writeString(x);
        parcel.writeString(y);
        parcel.writeString(longitudeHours);
        parcel.writeString(longitudeMinutes);
        parcel.writeString(longitudeSeconds);
        parcel.writeString(latitudeHours);
        parcel.writeString(latitudeMinutes);
        parcel.writeString(latitudeSeconds);
        parcel.writeString(longitudeSecondsDivide100);
        parcel.writeString(latitudeSecondsDivide100);
        parcel.writeString(midLandFcstCode);
        parcel.writeString(midTaCode);
    }

    public String getAdministrativeAreaCode()
    {
        return administrativeAreaCode;
    }

    public void setAdministrativeAreaCode(String administrativeAreaCode)
    {
        this.administrativeAreaCode = administrativeAreaCode;
    }

    public String getPhase1()
    {
        return phase1;
    }

    public void setPhase1(String phase1)
    {
        this.phase1 = phase1;
    }

    public String getPhase2()
    {
        return phase2;
    }

    public void setPhase2(String phase2)
    {
        this.phase2 = phase2;
    }

    public String getPhase3()
    {
        return phase3;
    }

    public void setPhase3(String phase3)
    {
        this.phase3 = phase3;
    }

    public String getX()
    {
        return x;
    }

    public void setX(String x)
    {
        this.x = x;
    }

    public String getY()
    {
        return y;
    }

    public void setY(String y)
    {
        this.y = y;
    }

    public String getLongitudeHours()
    {
        return longitudeHours;
    }

    public void setLongitudeHours(String longitudeHours)
    {
        this.longitudeHours = longitudeHours;
    }

    public String getLongitudeMinutes()
    {
        return longitudeMinutes;
    }

    public void setLongitudeMinutes(String longitudeMinutes)
    {
        this.longitudeMinutes = longitudeMinutes;
    }

    public String getLongitudeSeconds()
    {
        return longitudeSeconds;
    }

    public void setLongitudeSeconds(String longitudeSeconds)
    {
        this.longitudeSeconds = longitudeSeconds;
    }

    public String getLatitudeHours()
    {
        return latitudeHours;
    }

    public void setLatitudeHours(String latitudeHours)
    {
        this.latitudeHours = latitudeHours;
    }

    public String getLatitudeMinutes()
    {
        return latitudeMinutes;
    }

    public void setLatitudeMinutes(String latitudeMinutes)
    {
        this.latitudeMinutes = latitudeMinutes;
    }

    public String getLatitudeSeconds()
    {
        return latitudeSeconds;
    }

    public void setLatitudeSeconds(String latitudeSeconds)
    {
        this.latitudeSeconds = latitudeSeconds;
    }

    public String getLongitudeSecondsDivide100()
    {
        return longitudeSecondsDivide100;
    }

    public void setLongitudeSecondsDivide100(String longitudeSecondsDivide100)
    {
        this.longitudeSecondsDivide100 = longitudeSecondsDivide100;
    }

    public String getLatitudeSecondsDivide100()
    {
        return latitudeSecondsDivide100;
    }

    public void setLatitudeSecondsDivide100(String latitudeSecondsDivide100)
    {
        this.latitudeSecondsDivide100 = latitudeSecondsDivide100;
    }

    public String getMidLandFcstCode()
    {
        return midLandFcstCode;
    }

    public void setMidLandFcstCode(String midLandFcstCode)
    {
        this.midLandFcstCode = midLandFcstCode;
    }

    public String getMidTaCode()
    {
        return midTaCode;
    }

    public void setMidTaCode(String midTaCode)
    {
        this.midTaCode = midTaCode;
    }
}
