package com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.basicinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RealTime
{
    @Expose
    @SerializedName("holiday")
    private String holiday;

    @Expose
    @SerializedName("breaktime")
    private String breakTime;

    @Expose
    @SerializedName("open")
    private String open;

    @Expose
    @SerializedName("moreOpenOffInfoExists")
    private String moreOpenOffInfoExists;

    @Expose
    @SerializedName("datetime")
    private String dateTime;

    @Expose
    @SerializedName("currentPeriod")
    private CurrentPeriod currentPeriod;

    public RealTime()
    {
    }

    public String getHoliday()
    {
        return holiday;
    }

    public void setHoliday(String holiday)
    {
        this.holiday = holiday;
    }

    public String getBreakTime()
    {
        return breakTime;
    }

    public void setBreakTime(String breakTime)
    {
        this.breakTime = breakTime;
    }

    public String getOpen()
    {
        return open;
    }

    public void setOpen(String open)
    {
        this.open = open;
    }

    public String getMoreOpenOffInfoExists()
    {
        return moreOpenOffInfoExists;
    }

    public void setMoreOpenOffInfoExists(String moreOpenOffInfoExists)
    {
        this.moreOpenOffInfoExists = moreOpenOffInfoExists;
    }

    public String getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
    }

    public CurrentPeriod getCurrentPeriod()
    {
        return currentPeriod;
    }

    public void setCurrentPeriod(CurrentPeriod currentPeriod)
    {
        this.currentPeriod = currentPeriod;
    }
}
