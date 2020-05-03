package com.zerodsoft.tripweather.Room.DTO;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TravelScheduleCountTuple
{
    @PrimaryKey(autoGenerate = true)
    private int travel_id;

    @ColumnInfo(name = "count(travel_id)")
    private int count;

    public int getTravel_id()
    {
        return travel_id;
    }

    public void setTravel_id(int travel_id)
    {
        this.travel_id = travel_id;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }
}
