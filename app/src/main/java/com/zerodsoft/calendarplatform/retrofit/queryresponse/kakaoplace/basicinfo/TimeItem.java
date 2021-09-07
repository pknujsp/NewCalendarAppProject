package com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.basicinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TimeItem
{
    @Expose
    @SerializedName("timeName")
    private String timeName;

    @Expose
    @SerializedName("timeSE")
    private String timeSE;

    @Expose
    @SerializedName("dayOfWeek")
    private String dayOfWeek;

    public TimeItem()
    {
    }

    public String getTimeName()
    {
        return timeName;
    }

    public void setTimeName(String timeName)
    {
        this.timeName = timeName;
    }

    public String getTimeSE()
    {
        return timeSE;
    }

    public void setTimeSE(String timeSE)
    {
        this.timeSE = timeSE;
    }

    public String getDayOfWeek()
    {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek)
    {
        this.dayOfWeek = dayOfWeek;
    }
}
