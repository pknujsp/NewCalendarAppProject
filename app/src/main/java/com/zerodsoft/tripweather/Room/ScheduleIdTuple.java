package com.zerodsoft.tripweather.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ScheduleIdTuple
{
    @PrimaryKey
    @ColumnInfo(name = "schedule_id")
    private int scheduleId;

    public void setScheduleId(int scheduleId)
    {
        this.scheduleId = scheduleId;
    }

    public int getScheduleId()
    {
        return scheduleId;
    }
}
