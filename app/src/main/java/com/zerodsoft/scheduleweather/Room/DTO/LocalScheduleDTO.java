package com.zerodsoft.scheduleweather.Room.DTO;

public class LocalScheduleDTO
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

    public void setId(int id)
    {
        this.id = id;
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
