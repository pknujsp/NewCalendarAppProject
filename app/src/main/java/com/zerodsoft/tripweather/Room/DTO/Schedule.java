package com.zerodsoft.tripweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.zerodsoft.tripweather.Calendar.SelectedDate;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "schedule_table")
public class Schedule implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    private int schedule_id;

    @ColumnInfo(name = "schedule_parent_id")
    private int parentId;

    @ColumnInfo(name = "schedule_area_name")
    private String areaName;

    @ColumnInfo(name = "schedule_area_id")
    private int areaId;

    @ColumnInfo(name = "schedule_area_x")
    private String areaX;

    @ColumnInfo(name = "schedule_area_y")
    private String areaY;

    @ColumnInfo(name = "schedule_start_date")
    private String startDate;

    @ColumnInfo(name = "schedule_end_date")
    private String endDate;

    @Ignore
    private SelectedDate startDateObj;

    @Ignore
    private SelectedDate endDateObj;

    @Ignore
    private Area area;

    @Ignore
    private Date date;

    public void setDate(Date date)
    {
        this.date = date;
    }

    public Date getDate()
    {
        return date;
    }

    public Area getArea()
    {
        return area;
    }

    public void setArea(Area area)
    {
        this.area = area;
    }

    public SelectedDate getStartDateObj()
    {
        return startDateObj;
    }

    public void setStartDateObj(SelectedDate startDateObj)
    {
        this.startDateObj = startDateObj;
    }

    public SelectedDate getEndDateObj()
    {
        return endDateObj;
    }

    public void setEndDateObj(SelectedDate endDateObj)
    {
        this.endDateObj = endDateObj;
    }

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

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setAreaId(int areaId)
    {
        this.areaId = areaId;
    }

    public int getAreaId()
    {
        return areaId;
    }
}
