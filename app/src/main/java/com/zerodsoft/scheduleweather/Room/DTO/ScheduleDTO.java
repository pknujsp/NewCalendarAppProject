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

    public void setId(int id)
    {
        this.id = id;
    }

    public int getCategory()
    {
        return category;
    }

    public void setCategory(int category)
    {
        this.category = category;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
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

    public String getLocName()
    {
        return locName;
    }

    public void setLocName(String locName)
    {
        this.locName = locName;
    }

    public String getWthName()
    {
        return wthName;
    }

    public void setWthName(String wthName)
    {
        this.wthName = wthName;
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

    public String getLocX()
    {
        return locX;
    }

    public void setLocX(String locX)
    {
        this.locX = locX;
    }

    public String getLocY()
    {
        return locY;
    }

    public void setLocY(String locY)
    {
        this.locY = locY;
    }

    public int getNotiTime()
    {
        return notiTime;
    }

    public void setNotiTime(int notiTime)
    {
        this.notiTime = notiTime;
    }
}
