package com.zerodsoft.calendarplatform.retrofit.queryresponse.kakaoplace.basicinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OffDayItem
{
    @Expose
    @SerializedName("holidayName")
    private String holidayName;

    public OffDayItem()
    {
    }

    public String getHolidayName()
    {
        return holidayName;
    }

    public void setHolidayName(String holidayName)
    {
        this.holidayName = holidayName;
    }
}
