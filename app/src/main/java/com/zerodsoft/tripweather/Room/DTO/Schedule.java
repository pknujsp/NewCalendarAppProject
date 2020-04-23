package com.zerodsoft.tripweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "schedule_table")
public class Schedule implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    private int schedule_id;

    @ColumnInfo(name = "schedule_parent_id")
    private int parentId;

    @ColumnInfo(name = "schedule_area_name")
    private String areaName;

    @ColumnInfo(name = "schedule_area_x")
    private String areaX;

    @ColumnInfo(name = "schedule_area_y")
    private String areaY;

    @ColumnInfo(name = "schedule_start_date")
    private int startDate;

    @ColumnInfo(name = "schedule_end_date")
    private int endDate;

    public int getSchedule_id()
    {
        return schedule_id;
    }

    public void setSchedule_id(int schedule_id)
    {
        this.schedule_id = schedule_id;
    }

    public int getParentId()
    {
        return parentId;
    }

    public void setParentId(int parentId)
    {
        this.parentId = parentId;
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

    public int getStartDate()
    {
        return startDate;
    }

    public void setStartDate(int startDate)
    {
        this.startDate = startDate;
    }

    public int getEndDate()
    {
        return endDate;
    }

    public void setEndDate(int endDate)
    {
        this.endDate = endDate;
    }
}
