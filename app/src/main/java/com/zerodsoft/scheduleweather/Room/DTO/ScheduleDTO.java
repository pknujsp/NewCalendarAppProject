package com.zerodsoft.scheduleweather.Room.DTO;

public class ScheduleDTO
{
    private int id;
    private String subject;
    private String content;
    private int startDate;
    private int endDate;
    private String locName;
    private String wthName;
    private String locLat;
    private String locLon;
    private String locX;
    private String locY;
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
