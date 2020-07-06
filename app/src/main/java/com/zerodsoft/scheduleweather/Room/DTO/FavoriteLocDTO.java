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

    public String getLocName()
    {
        return locName;
    }

    public void setLocName(String locName)
    {
        this.locName = locName;
    }

    public String getLocCategory()
    {
        return locCategory;
    }

    public void setLocCategory(String locCategory)
    {
        this.locCategory = locCategory;
    }

    public String getLocLat()
    {
        return locLat;
    }

    public void setLocLat(String locLat)
    {
        this.locLat = locLat;
    }

    public String getLocLon()
    {
        return locLon;
    }

    public void setLocLon(String locLon)
    {
        this.locLon = locLon;
    }
}
