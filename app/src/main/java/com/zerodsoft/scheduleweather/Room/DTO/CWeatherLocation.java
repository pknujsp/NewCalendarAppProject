package com.zerodsoft.scheduleweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "cweather_location_table")
public class CWeatherLocation implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "location_id")
    private int id;

    @ColumnInfo(name = "location_area_name")
    private String areaName;

    @ColumnInfo(name = "location_x")
    private String areaX;

    @ColumnInfo(name = "location_y")
    private String areaY;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getAreaName()
    {
        return areaName;
    }

    public void setAreaName(String areaName)
    {
        this.areaName = areaName;
    }

    public String getAreaX()
    {
        return areaX;
    }

    public void setAreaX(String areaX)
    {
        this.areaX = areaX;
    }

    public String getAreaY()
    {
        return areaY;
    }

    public void setAreaY(String areaY)
    {
        this.areaY = areaY;
    }
}
