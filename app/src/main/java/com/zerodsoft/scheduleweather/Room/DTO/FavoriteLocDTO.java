package com.zerodsoft.scheduleweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "TB_FAVORITE_LOC"
        , foreignKeys = @ForeignKey(entity = ScheduleDTO.class,
        parentColumns = "id",
        childColumns = "schedule_id"))
public class FavoriteLocDTO implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "schedule_id")
    private int scheduleId;

    @ColumnInfo(name = "loc_name")
    private String locName;

    @ColumnInfo(name = "loc_category")
    private String locCategory;

    @ColumnInfo(name = "loc_lat")
    private String locLat;

    @ColumnInfo(name = "loc_lon")
    private String locLon;

    public int getId()
    {
        return id;
    }

    public FavoriteLocDTO setId(int id)
    {
        this.id = id;
        return this;
    }

    public int getScheduleId()
    {
        return scheduleId;
    }

    public FavoriteLocDTO setScheduleId(int scheduleId)
    {
        this.scheduleId = scheduleId;
        return this;
    }

    public String getLocName()
    {
        return locName;
    }

    public FavoriteLocDTO setLocName(String locName)
    {
        this.locName = locName;
        return this;
    }

    public String getLocCategory()
    {
        return locCategory;
    }

    public FavoriteLocDTO setLocCategory(String locCategory)
    {
        this.locCategory = locCategory;
        return this;
    }

    public String getLocLat()
    {
        return locLat;
    }

    public FavoriteLocDTO setLocLat(String locLat)
    {
        this.locLat = locLat;
        return this;
    }

    public String getLocLon()
    {
        return locLon;
    }

    public FavoriteLocDTO setLocLon(String locLon)
    {
        this.locLon = locLon;
        return this;
    }
}
