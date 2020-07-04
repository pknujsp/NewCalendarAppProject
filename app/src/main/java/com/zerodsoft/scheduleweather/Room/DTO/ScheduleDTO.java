package com.zerodsoft.scheduleweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "TB_SCHEDULE",
        foreignKeys = @ForeignKey(
                entity = ScheduleCategoryDTO.class,
                parentColumns = "id",
                childColumns = "category"
        ))

public class ScheduleDTO implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "category")
    private int category;

    @ColumnInfo(name = "subject")
    private String subject;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "start_date")
    private int startDate;

    @ColumnInfo(name = "end_date")
    private int endDate;

    @ColumnInfo(name = "loc_name")
    private String locName;

    @ColumnInfo(name = "wth_name")
    private String wthName;

    @ColumnInfo(name = "loc_lat")
    private String locLat;

    @ColumnInfo(name = "loc_lon")
    private String locLon;

    @ColumnInfo(name = "loc_x")
    private String locX;

    @ColumnInfo(name = "loc_y")
    private String locY;

    @ColumnInfo(name = "noti_time")
    private int notiTime;

    public int getId()
    {
        return id;
    }

    public ScheduleDTO setId(int id)
    {
        this.id = id;
        return this;
    }

    public ScheduleDTO setCategory(int category)
    {
        this.category = category;
        return this;
    }

    public int getCategory()
    {
        return category;
    }

    public String getSubject()
    {
        return subject;
    }

    public ScheduleDTO setSubject(String subject)
    {
        this.subject = subject;
        return this;
    }

    public String getContent()
    {
        return content;
    }

    public ScheduleDTO setContent(String content)
    {
        this.content = content;
        return this;
    }

    public int getStartDate()
    {
        return startDate;
    }

    public ScheduleDTO setStartDate(int startDate)
    {
        this.startDate = startDate;
        return this;
    }

    public int getEndDate()
    {
        return endDate;
    }

    public ScheduleDTO setEndDate(int endDate)
    {
        this.endDate = endDate;
        return this;
    }

    public String getLocName()
    {
        return locName;
    }

    public ScheduleDTO setLocName(String locName)
    {
        this.locName = locName;
        return this;
    }

    public String getWthName()
    {
        return wthName;
    }

    public ScheduleDTO setWthName(String wthName)
    {
        this.wthName = wthName;
        return this;
    }

    public String getLocLat()
    {
        return locLat;
    }

    public ScheduleDTO setLocLat(String locLat)
    {
        this.locLat = locLat;
        return this;
    }

    public String getLocLon()
    {
        return locLon;
    }

    public ScheduleDTO setLocLon(String locLon)
    {
        this.locLon = locLon;
        return this;
    }

    public String getLocX()
    {
        return locX;
    }

    public ScheduleDTO setLocX(String locX)
    {
        this.locX = locX;
        return this;
    }

    public String getLocY()
    {
        return locY;
    }

    public ScheduleDTO setLocY(String locY)
    {
        this.locY = locY;
        return this;
    }

    public int getNotiTime()
    {
        return notiTime;
    }

    public ScheduleDTO setNotiTime(int notiTime)
    {
        this.notiTime = notiTime;
        return this;
    }
}
