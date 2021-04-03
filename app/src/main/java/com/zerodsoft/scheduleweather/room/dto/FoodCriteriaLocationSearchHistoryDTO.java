package com.zerodsoft.scheduleweather.room.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_criteria_location_search_history_table")
public class FoodCriteriaLocationSearchHistoryDTO
{
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "calendar_id")
    private Integer calendarId;

    @ColumnInfo(name = "event_id")
    private Long eventId;

    @ColumnInfo(name = "instance_id")
    private Long instanceId;

    @ColumnInfo(name = "place_name")
    private String placeName;

    @ColumnInfo(name = "address_name")
    private String addressName;

    @ColumnInfo(name = "road_address_name")
    private String roadAddressName;

    @ColumnInfo(name = "latitude")
    private String latitude;

    @ColumnInfo(name = "longitude")
    private String longitude;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Integer getCalendarId()
    {
        return calendarId;
    }

    public void setCalendarId(Integer calendarId)
    {
        this.calendarId = calendarId;
    }

    public Long getEventId()
    {
        return eventId;
    }

    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
    }

    public Long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public String getPlaceName()
    {
        return placeName;
    }

    public void setPlaceName(String placeName)
    {
        this.placeName = placeName;
    }

    public String getAddressName()
    {
        return addressName;
    }

    public void setAddressName(String addressName)
    {
        this.addressName = addressName;
    }

    public String getRoadAddressName()
    {
        return roadAddressName;
    }

    public void setRoadAddressName(String roadAddressName)
    {
        this.roadAddressName = roadAddressName;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }
}
