package com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.basicinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentPeriod
{
    @Expose
    @SerializedName("periodName")
    private String periodName;

    @Expose
    @SerializedName("timeList")
    private List<TimeItem> timeItemList;

    public CurrentPeriod()
    {
    }

    public String getPeriodName()
    {
        return periodName;
    }

    public void setPeriodName(String periodName)
    {
        this.periodName = periodName;
    }

    public List<TimeItem> getTimeItemList()
    {
        return timeItemList;
    }

    public void setTimeItemList(List<TimeItem> timeItemList)
    {
        this.timeItemList = timeItemList;
    }
}
