package com.zerodsoft.scheduleweather.room.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_criteria_location_info_table")
public class FoodCriteriaLocationInfoDTO
{
    @Ignore
    public static final int TYPE_SELECTED_LOCATION = 0;
    @Ignore
    public static final int TYPE_CURRENT_LOCATION = 1;
    @Ignore
    public static final int TYPE_CUSTOM_SELECTED_LOCATION = 2;

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "calendar_id")
    private int calendarId;

    @ColumnInfo(name = "event_id")
    private long eventId;

    @ColumnInfo(name = "instance_id")
    private long instanceId;

    @ColumnInfo(name = "using_type")
    private int usingType;

    @ColumnInfo(name = "history_location_id")
    private int historyLocationId;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getUsingType()
    {
        return usingType;
    }

    public void setUsingType(int usingType)
    {
        this.usingType = usingType;
    }

    public int getCalendarId()
    {
        return calendarId;
    }

    public void setCalendarId(int calendarId)
    {
        this.calendarId = calendarId;
    }

    public long getEventId()
    {
        return eventId;
    }

    public void setEventId(long eventId)
    {
        this.eventId = eventId;
    }

    public long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(long instanceId)
    {
        this.instanceId = instanceId;
    }

    public int getHistoryLocationId()
    {
        return historyLocationId;
    }

    public void setHistoryLocationId(int historyLocationId)
    {
        this.historyLocationId = historyLocationId;
    }
}
