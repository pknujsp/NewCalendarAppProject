package com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.basicinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OpenHour
{
    @Expose
    @SerializedName("realtime")
    private RealTime realTime;

    @Expose
    @SerializedName("offdayList")
    private List<OffDayItem> offDayList;

    public OpenHour()
    {
    }

    public RealTime getRealTime()
    {
        return realTime;
    }

    public void setRealTime(RealTime realTime)
    {
        this.realTime = realTime;
    }

    public List<OffDayItem> getOffDayList()
    {
        return offDayList;
    }

    public void setOffDayList(List<OffDayItem> offDayList)
    {
        this.offDayList = offDayList;
    }
}
