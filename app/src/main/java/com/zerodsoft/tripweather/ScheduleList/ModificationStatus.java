package com.zerodsoft.tripweather.ScheduleList;

public class ModificationStatus
{
    private int scheduleId;
    private int status;


    public int getStatus()
    {
        return status;
    }

    public ModificationStatus setStatus(int status)
    {
        this.status = status;
        return this;
    }

    public ModificationStatus setScheduleId(int scheduleId)
    {
        this.scheduleId = scheduleId;
        return this;
    }

    public int getScheduleId()
    {
        return scheduleId;
    }
}
